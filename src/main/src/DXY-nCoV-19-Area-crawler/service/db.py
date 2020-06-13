"""
@ProjectName: DXY-2019-nCov-Crawler
@FileName: db.py
@Author: BeamatchJ
@Date: 2020/6/12
"""
import psycopg2
import time
import datetime

#Your database information
database = 'chn_test'
user = 'postgres'
password = 'xiong123'
host = '47.94.150.127'
port = 5432
# the name of your table to store cov_19_area data
data_table_name = "cov_19_area"

class DB:
    def __init__(self):
        self.data_table_name = data_table_name
        pass

    def connect_db(self):
        try:
            self.conn = psycopg2.connect(database=database,
                                         user=user,
                                         password=password,
                                         host=host,
                                         port=port)
        except Exception as e:
            error_logger.error(e)
        else:
            return self.conn
        return None

    def close_db_connection(self):
        self.conn.commit()
        self.conn.close()

    def create_db_table(self):
        table_name = self.data_table_name
        cur = self.conn.cursor()
        # postgreSQL is a RDBS ,design table structure first
        # execute sql to create a table if it doesn't exist
        cur.execute("create table if not exists %s(id serial not null ," \
                    "cityName varchar(30),currentConfirmedCount int," \
                    "confirmedCount int, suspectedCount int,curedCount int,deadCount int," \
                    "locationId int unique , updateTime timestamp, provinceName varchar(30))" % (table_name))
        # set column locationId unique
        cur.close()
        #print("table created successfully")

    def insert(self, rowdata):
        table_name = self.data_table_name
        cityName = rowdata['cityName']
        currentConfirmedCount = rowdata['currentConfirmedCount']
        confirmedCount = rowdata['confirmedCount']
        suspectedCount = rowdata['suspectedCount']
        curedCount = rowdata['curedCount']
        deadCount = rowdata['deadCount']
        locationId = rowdata['locationId']
        # database's type timestamp need a String like this, timestamp is converted to localtime
        updateTime = datetime.datetime.fromtimestamp(float(rowdata['updateTime'])/1000.0).strftime("%Y-%m-%d %H:%M:%S.%f")
        provinceName = rowdata['provinceName']

        cur = self.conn.cursor()
        # execute sql to update if locationId exists, otherwise insert a row data
        # %s need ''
        cur.execute("insert into %s(cityName,currentConfirmedCount,confirmedCount," \
                    "suspectedCount,curedCount,deadCount,locationId,updateTime,provinceName) " \
                    "values('%s',%d,%d,%d,%d,%d,%d,'%s','%s') on conflict(locationId) " \
                    "do update set cityName='%s',currentConfirmedCount=%d,confirmedCount=%d," \
                    "suspectedCount=%d,curedCount=%d,deadCount=%d,updateTime='%s',provinceName='%s';"
                    % (table_name,cityName,currentConfirmedCount,confirmedCount,suspectedCount,
                       curedCount,deadCount,locationId,updateTime,provinceName,  ## insert
                       cityName,currentConfirmedCount,confirmedCount,suspectedCount,
                       curedCount,deadCount,updateTime,provinceName))  ##update
        cur.close()
        #print("insert or update successfully")
