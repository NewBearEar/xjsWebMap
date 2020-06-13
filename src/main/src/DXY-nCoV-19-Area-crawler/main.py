"""
@ProjectName: DXY-COVID-19-Area-PostgreSQL
@FileName: main.py
@Author: BeamatchJ
@Date: 2020/6/12
"""
from service.crawler import Crawler


if __name__ == '__main__':
    crawler = Crawler()
    print("Start Crawler!")
    crawler.run()
