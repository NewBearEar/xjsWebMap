"""
@ProjectName: DXY-2019-nCov-Crawler
@FileName: crawler.py
@Author: BeamatchJ
@Date: 2020/6/12
"""
from bs4 import BeautifulSoup
from service.db import DB
from service.userAgent import user_agent_list
from service.nameMap import country_type_map, city_name_map, country_name_map, continent_name_map
import re
import json
import time
import random
import logging
import requests


logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(message)s')
logger = logging.getLogger(__name__)


class Crawler:
    def __init__(self):
        self.session = requests.session()
        self.db = DB()
        self.crawl_timestamp = int()

    def run(self):
        # because I need run the crawler in Java, don't nead regularly perform here
        #while True:
        self.crawler()
            #time.sleep(60*30) #time internal 30min

    def crawler(self):
        while True:
            self.session.headers.update(
                {
                    'user-agent': random.choice(user_agent_list)
                }
            )
            self.crawl_timestamp = int(time.time() * 1000)

            try:
                r = self.session.get(url='https://ncov.dxy.cn/ncovh5/view/pneumonia')
            except requests.exceptions.ChunkedEncodingError:
                continue
            soup = BeautifulSoup(r.content, 'lxml')

            area_information = re.search(r'\[(.*)\]', str(soup.find('script', attrs={'id': 'getAreaStat'})))
            if area_information:
                self.area_parser(area_information=area_information)
            if not area_information:
                time.sleep(3)
                continue
            break

        logger.info('Successfully crawled.')

    def area_parser(self, area_information):
        area_information = json.loads(area_information.group(0))
        self.db.connect_db()
        self.db.create_db_table()
        for area in area_information:
            # used to hold each record
            rowdata = {}
            cities_backup = area.pop('cities')
            area['cities'] = cities_backup
            area['countryName'] = '中国'
            area['countryEnglishName'] = 'China'
            area['continentName'] = '亚洲'
            area['continentEnglishName'] = 'Asia'
            area['provinceEnglishName'] = city_name_map[area['provinceShortName']]['engName']
            area['updateTime'] = self.crawl_timestamp
            # add data to rowdata
            rowdata['updateTime'] = area['updateTime']
            rowdata['provinceName'] = area['provinceName']
            # If cities is not empty, parse every city in it.
            if cities_backup:
                for city in area['cities']:
                    if city['cityName'] != '待明确地区':
                        try:
                            city['cityEnglishName'] = city_name_map[area['provinceShortName']]['cities'][city['cityName']]
                            rowdata['cityName'] = city['cityName']
                            rowdata['currentConfirmedCount'] = city['currentConfirmedCount']
                            rowdata['confirmedCount'] = city['confirmedCount']
                            rowdata['suspectedCount'] = city['suspectedCount']
                            rowdata['curedCount'] = city['curedCount']
                            rowdata['deadCount'] = city['deadCount']
                            rowdata['locationId'] = city['locationId']
                            # add data into database
                            self.db.insert(rowdata)
                        except KeyError:
                            print(area['provinceShortName'], city['cityName'])
                            pass
                    else:
                        city['cityEnglishName'] = 'Area not defined'

            else:
                try:
                    rowdata['cityName'] = area['provinceName']
                    rowdata['currentConfirmedCount'] = area['currentConfirmedCount']
                    rowdata['confirmedCount'] = area['confirmedCount']
                    rowdata['suspectedCount'] = area['suspectedCount']
                    rowdata['curedCount'] = area['curedCount']
                    rowdata['deadCount'] = area['deadCount']
                    rowdata['locationId'] = area['locationId']
                    self.db.insert(rowdata)
                except KeyError:
                    print("No such key")
                    pass

        self.db.close_db_connection()

if __name__ == '__main__':
    crawler = Crawler()
    crawler.run()