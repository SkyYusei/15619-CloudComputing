-- 15619 Cloud Computing 
-- SQL script for Runner
-- Each section is identified by the following start and end comments,
-- -- start <section_name>
-- <SQL commands delimited with ;>
-- -- end <section_name>
-- Please do not remove or modify any existing section except for 
-- "redshift_create_table_optimized", "query1_opt", "query2_opt"
-- and "query3_opt" sections. Please be aware that
-- the "redshift_create_table_optimized" has "diststyle all".
-- Please change this distribution style for one pair of tables based
-- on your analysis. Please select your sort keys for all tables too.

-- start query1
select sum(lo_extendedprice*lo_discount) as revenue from lineorder, dwdate 
where lo_orderdate=d_datekey 
and d_year=1997 
and lo_discount between 1 and 3 
and lo_quantity < 24;
-- end query1

-- start query2
select sum(lo_revenue), d_year, p_brand1 from lineorder, dwdate, part, supplier
where lo_orderdate = d_datekey 
and lo_partkey = p_partkey 
and lo_suppkey = s_suppkey
and p_category = 'MFGR#12' 
and s_region = 'AMERICA' 
group by d_year, p_brand1 order by d_year, p_brand1 limit 500;
-- end query2

-- start query3
select c_city, s_city, d_year, sum(lo_revenue) as revenue from 
customer, lineorder,  supplier, dwdate 
where lo_custkey = c_custkey 
and lo_suppkey = s_suppkey and lo_orderdate = d_datekey 
and (c_city='UNITED KI1' or c_city='UNITED KI5') 
and (s_city='UNITED KI1' or s_city='UNITED KI5') 
and d_yearmonth = 'Dec1997' 
group by c_city, s_city, d_year order by d_year asc, revenue desc limit 5;
-- end query3

-- start query1_opt
select sum(lo_extendedprice*lo_discount) as revenue from lineorder_opt, dwdate_opt 
where lo_orderdate=d_datekey 
and d_year=1997 
and lo_discount between 1 and 3 
and lo_quantity < 24;
-- end query1_opt

-- start query2_opt
select sum(lo_revenue), d_year, p_brand1 from lineorder_opt, dwdate_opt, part_opt, supplier_opt
where lo_orderdate = d_datekey 
and lo_partkey = p_partkey 
and lo_suppkey = s_suppkey
and p_category = 'MFGR#12' 
and s_region = 'AMERICA' 
group by d_year, p_brand1 order by d_year, p_brand1 limit 500;
-- end query2_opt

-- start query3_opt
select c_city, s_city, d_year, sum(lo_revenue) as revenue from 
customer_opt, lineorder_opt, supplier_opt, dwdate_opt 
where lo_custkey = c_custkey 
and lo_suppkey = s_suppkey and lo_orderdate = d_datekey 
and (c_city='UNITED KI1' or c_city='UNITED KI5') 
and (s_city='UNITED KI1' or s_city='UNITED KI5') 
and d_yearmonth = 'Dec1997' 
group by c_city, s_city, d_year order by d_year asc, revenue desc limit 5;
-- end query3_opt

-- start describe_tables
describe part;
describe supplier;
describe customer;
describe dwdate;
describe lineorder;
-- end describe_tables

-- start count_tables
select count(*) from LINEORDER;
select count(*) from PART;
select count(*) from  CUSTOMER;
select count(*) from  SUPPLIER;
select count(*) from  DWDATE;
-- end count_tables

-- start count_tables_opt
select count(*) as lineorder_count from  lineorder_opt;
select count(*) as part_count from  part_opt;
select count(*) as customer_count from  customer_opt;
select count(*) as supplier_count from  supplier_opt;
select count(*) as dwdate_count from  dwdate_opt;
-- end count_tables_opt

-- start drop_tables
drop table part;
drop table supplier;
drop table customer;
drop table dwdate;
drop table lineorder;
-- end drop_tables

-- start hive_create_table
CREATE EXTERNAL TABLE part (
	p_partkey INT, 
	p_name STRING, 
	p_mfgr STRING, 
	p_category STRING, 
	p_brand1 STRING, 
	p_color STRING, 
	p_type STRING, 
	p_size INT, 
	p_container STRING)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '|'
LOCATION 's3://cmucc-public/p35/ssbgz/part/';

CREATE EXTERNAL TABLE supplier (
	s_suppkey   INT,
	s_name STRING,
	s_address STRING,
	s_city STRING,
	s_nation STRING,
	s_region STRING,
	s_phone STRING)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '|'
LOCATION 's3://cmucc-public/p35/ssbgz/supplier/';

CREATE EXTERNAL TABLE customer(
	c_custkey INT,
	c_name STRING,
	c_address  STRING,
	c_city STRING,
	c_nation STRING,
	c_region STRING,
	c_phone STRING,
	c_mktsegment STRING)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '|'
LOCATION 's3://cmucc-public/p35/ssbgz/customer/';

CREATE EXTERNAL TABLE dwdate(
	d_datekey INT,
	d_date STRING,
	d_dayofweek STRING,
	d_month STRING,
	d_year INT,
	d_yearmonthnum INT,
	d_yearmonth STRING,
	d_daynuminweek INT,
	d_daynuminmonth INT,
	d_daynuminyear INT,
	d_monthnuminyear INT,
	d_weeknuminyear INT,
	d_sellingseason STRING,
	d_lastdayinweekfl STRING,
	d_lastdayinmonthfl STRING,
	d_holidayfl STRING,
	d_weekdayfl STRING)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '|'
LOCATION 's3://cmucc-public/p35/ssbgz/dwdate/';

CREATE EXTERNAL TABLE lineorder(
	lo_orderkey INT,
	lo_linenumber INT,
	lo_custkey INT,
	lo_partkey INT,
	lo_suppkey INT,
	lo_orderdate INT,
	lo_orderpriority STRING,
	lo_shippriority STRING,
	lo_quantity INT,
	lo_extendedprice INT,
	lo_ordertotalprice INT,
	lo_discount INT,
	lo_revenue INT,
	lo_supplycost INT,
	lo_tax INT,
	lo_commitdate INT,
	lo_shipmode STRING)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '|'
LOCATION 's3://cmucc-public/p35/ssbgz/lineorder/';
-- end hive_create_table

-- start impala_create_table_unoptimized
CREATE EXTERNAL TABLE part (
	p_partkey INT, 
	p_name STRING, 
	p_mfgr STRING, 
	p_category STRING, 
	p_brand1 STRING, 
	p_color STRING, 
	p_type STRING, 
	p_size INT, 
	p_container STRING)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '|'
LOCATION '/data/part/';

CREATE EXTERNAL TABLE supplier (
	s_suppkey   INT,
	s_name STRING,
	s_address STRING,
	s_city STRING,
	s_nation STRING,
	s_region STRING,
	s_phone STRING)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '|'
LOCATION '/data/supplier/';

CREATE EXTERNAL TABLE customer(
	c_custkey INT,
	c_name STRING,
	c_address  STRING,
	c_city STRING,
	c_nation STRING,
	c_region STRING,
	c_phone STRING,
	c_mktsegment STRING)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '|'
LOCATION '/data/customer/';

CREATE EXTERNAL TABLE dwdate(
	d_datekey INT,
	d_date STRING,
	d_dayofweek STRING,
	d_month STRING,
	d_year INT,
	d_yearmonthnum INT,
	d_yearmonth STRING,
	d_daynuminweek INT,
	d_daynuminmonth INT,
	d_daynuminyear INT,
	d_monthnuminyear INT,
	d_weeknuminyear INT,
	d_sellingseason STRING,
	d_lastdayinweekfl STRING,
	d_lastdayinmonthfl STRING,
	d_holidayfl STRING,
	d_weekdayfl STRING)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '|'
LOCATION '/data/dwdate/';

CREATE EXTERNAL TABLE lineorder(
	lo_orderkey INT,
	lo_linenumber INT,
	lo_custkey INT,
	lo_partkey INT,
	lo_suppkey INT,
	lo_orderdate INT,
	lo_orderpriority STRING,
	lo_shippriority STRING,
	lo_quantity INT,
	lo_extendedprice INT,
	lo_ordertotalprice INT,
	lo_discount INT,
	lo_revenue INT,
	lo_supplycost INT,
	lo_tax INT,
	lo_commitdate INT,
	lo_shipmode STRING)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '|'
LOCATION '/data/lineorder/';
-- end impala_create_table_unoptimized

-- start redshift_create_table_unoptimized
CREATE TABLE part 
(
  p_partkey     INTEGER NOT NULL,
  p_name        VARCHAR(22) NOT NULL,
  p_mfgr        VARCHAR(6) NOT NULL,
  p_category    VARCHAR(7) NOT NULL,
  p_brand1      VARCHAR(9) NOT NULL,
  p_color       VARCHAR(11) NOT NULL,
  p_type        VARCHAR(25) NOT NULL,
  p_size        INTEGER NOT NULL,
  p_container   VARCHAR(10) NOT NULL
);
CREATE TABLE supplier 
(
  s_suppkey   INTEGER NOT NULL,
  s_name      VARCHAR(25) NOT NULL,
  s_address   VARCHAR(25) NOT NULL,
  s_city      VARCHAR(10) NOT NULL,
  s_nation    VARCHAR(15) NOT NULL,
  s_region    VARCHAR(12) NOT NULL,
  s_phone     VARCHAR(15) NOT NULL
);
CREATE TABLE customer 
(
  c_custkey      INTEGER NOT NULL,
  c_name         VARCHAR(25) NOT NULL,
  c_address      VARCHAR(25) NOT NULL,
  c_city         VARCHAR(10) NOT NULL,
  c_nation       VARCHAR(15) NOT NULL,
  c_region       VARCHAR(12) NOT NULL,
  c_phone        VARCHAR(15) NOT NULL,
  c_mktsegment   VARCHAR(10) NOT NULL
);
CREATE TABLE dwdate 
(
  d_datekey            INTEGER NOT NULL,
  d_date               VARCHAR(19) NOT NULL,
  d_dayofweek          VARCHAR(10) NOT NULL,
  d_month              VARCHAR(10) NOT NULL,
  d_year               INTEGER NOT NULL,
  d_yearmonthnum       INTEGER NOT NULL,
  d_yearmonth          VARCHAR(8) NOT NULL,
  d_daynuminweek       INTEGER NOT NULL,
  d_daynuminmonth      INTEGER NOT NULL,
  d_daynuminyear       INTEGER NOT NULL,
  d_monthnuminyear     INTEGER NOT NULL,
  d_weeknuminyear      INTEGER NOT NULL,
  d_sellingseason      VARCHAR(13) NOT NULL,
  d_lastdayinweekfl    VARCHAR(1) NOT NULL,
  d_lastdayinmonthfl   VARCHAR(1) NOT NULL,
  d_holidayfl          VARCHAR(1) NOT NULL,
  d_weekdayfl          VARCHAR(1) NOT NULL
);
CREATE TABLE lineorder 
(
  lo_orderkey          INTEGER NOT NULL,
  lo_linenumber        INTEGER NOT NULL,
  lo_custkey           INTEGER NOT NULL,
  lo_partkey           INTEGER NOT NULL,
  lo_suppkey           INTEGER NOT NULL,
  lo_orderdate         INTEGER NOT NULL,
  lo_orderpriority     VARCHAR(15) NOT NULL,
  lo_shippriority      VARCHAR(1) NOT NULL,
  lo_quantity          INTEGER NOT NULL,
  lo_extendedprice     INTEGER NOT NULL,
  lo_ordertotalprice   INTEGER NOT NULL,
  lo_discount          INTEGER NOT NULL,
  lo_revenue           INTEGER NOT NULL,
  lo_supplycost        INTEGER NOT NULL,
  lo_tax               INTEGER NOT NULL,
  lo_commitdate        INTEGER NOT NULL,
  lo_shipmode          VARCHAR(10) NOT NULL
);
-- end redshift_create_table_unoptimized

-- start redshift_load_uncompressed
copy customer from 's3://awssampledb/ssbgz/customer' 
credentials 'aws_access_key_id=<AWS_ACCESS_KEY_ID>;aws_secret_access_key=<AWS_SECRET_ACCESS_KEY>' gzip compupdate off;

copy dwdate from 's3://awssampledb/ssbgz/dwdate' 
credentials 'aws_access_key_id=<AWS_ACCESS_KEY_ID>;aws_secret_access_key=<AWS_SECRET_ACCESS_KEY>' gzip compupdate off;

copy lineorder from 's3://awssampledb/ssbgz/lineorder' 
credentials 'aws_access_key_id=<AWS_ACCESS_KEY_ID>;aws_secret_access_key=<AWS_SECRET_ACCESS_KEY>' gzip compupdate off;

copy part from 's3://awssampledb/ssbgz/part' 
credentials 'aws_access_key_id=<AWS_ACCESS_KEY_ID>;aws_secret_access_key=<AWS_SECRET_ACCESS_KEY>' gzip compupdate off;

copy supplier from 's3://awssampledb/ssbgz/supplier' 
credentials 'aws_access_key_id=<AWS_ACCESS_KEY_ID>;aws_secret_access_key=<AWS_SECRET_ACCESS_KEY>' gzip compupdate off;
-- end redshift_load_uncompressed

-- start redshift_table_space
select stv_tbl_perm.name as table, count(*) as mb
from stv_blocklist, stv_tbl_perm
where stv_blocklist.tbl = stv_tbl_perm.id
and stv_blocklist.slice = stv_tbl_perm.slice
and stv_tbl_perm.name in ('lineorder','part','customer','dwdate','supplier')
group by stv_tbl_perm.name
order by 1 asc;
-- end redshift_table_space

-- start explain_query2
explain
select sum(lo_revenue), d_year, p_brand1 from lineorder, dwdate, part, supplier
where lo_orderdate = d_datekey 
and lo_partkey = p_partkey 
and lo_suppkey = s_suppkey
and p_category = 'MFGR#12' 
and s_region = 'AMERICA' 
group by d_year, p_brand1 order by d_year, p_brand1;
-- end explain_query2

-- start redshift_lineorder_compression_encoding
select col, max(blocknum)
from stv_blocklist b, stv_tbl_perm p
where (b.tbl=p.id) and name ='lineorder'
and col < 6
group by name, col
order by col;
-- end redshift_lineorder_compression_encoding

-- start analyze_compression
analyze compression lineorder;
-- end analyze_compression

-- start redshift_create_table_optimized
CREATE TABLE part (
  p_partkey     	integer     	not null,
  p_name        	varchar(22) 	not null,
  p_mfgr        	varchar(6)      not null,
  p_category    	varchar(7)      not null,
  p_brand1      	varchar(9)      not null,
  p_color       	varchar(11) 	not null,
  p_type        	varchar(25) 	not null,
  p_size        	integer     	not null,
  p_container   	varchar(10)     not null
);
CREATE TABLE supplier (
  s_suppkey     	integer        not null,
  s_name        	varchar(25)    not null,
  s_address     	varchar(25)    not null,
  s_city        	varchar(10)    not null,
  s_nation      	varchar(15)    not null,
  s_region      	varchar(12)    not null,
  s_phone       	varchar(15)    not null
);
CREATE TABLE customer (
  c_custkey     	integer        not null,
  c_name        	varchar(25)    not null,
  c_address     	varchar(25)    not null,
  c_city        	varchar(10)    not null,
  c_nation      	varchar(15)    not null,
  c_region      	varchar(12)    not null,
  c_phone       	varchar(15)    not null,
  c_mktsegment      varchar(10)    not null
);
CREATE TABLE dwdate (
  d_datekey            integer       not null,
  d_date               varchar(19)   not null,
  d_dayofweek	      varchar(10)   not null,
  d_month      	    varchar(10)   not null,
  d_year               integer       not null,
  d_yearmonthnum       integer  	 not null,
  d_yearmonth          varchar(8)	not null,
  d_daynuminweek       integer       not null,
  d_daynuminmonth      integer       not null,
  d_daynuminyear       integer       not null,
  d_monthnuminyear     integer       not null,
  d_weeknuminyear      integer       not null,
  d_sellingseason      varchar(13)    not null,
  d_lastdayinweekfl    varchar(1)    not null,
  d_lastdayinmonthfl   varchar(1)    not null,
  d_holidayfl          varchar(1)    not null,
  d_weekdayfl          varchar(1)    not null
);
CREATE TABLE lineorder (
  lo_orderkey      	integer         not null,
  lo_linenumber        	integer     	not null,
  lo_custkey           	integer     	not null,
  lo_partkey           	integer     	not null,
  lo_suppkey           	integer     	not null,
  lo_orderdate         	integer     	not null,
  lo_orderpriority     	varchar(15)     not null,
  lo_shippriority      	varchar(1)      not null,
  lo_quantity          	integer     	not null,
  lo_extendedprice     	integer     	not null,
  lo_ordertotalprice   	integer     	not null,
  lo_discount          	integer     	not null,
  lo_revenue           	integer     	not null,
  lo_supplycost        	integer     	not null,
  lo_tax               	integer     	not null,
  lo_commitdate         integer         not null,
  lo_shipmode          	varchar(10)     not null
);
-- end redshift_create_table_optimized

-- start redshift_load_compressed
copy customer from 's3://awssampledb/ssbgz/customer' 
credentials 'aws_access_key_id=<AWS_ACCESS_KEY_ID>;aws_secret_access_key=<AWS_SECRET_ACCESS_KEY>' gzip;

copy dwdate from 's3://awssampledb/ssbgz/dwdate' 
credentials 'aws_access_key_id=<AWS_ACCESS_KEY_ID>;aws_secret_access_key=<AWS_SECRET_ACCESS_KEY>' gzip;

copy lineorder from 's3://awssampledb/ssbgz/lineorder' 
credentials 'aws_access_key_id=<AWS_ACCESS_KEY_ID>;aws_secret_access_key=<AWS_SECRET_ACCESS_KEY>' gzip;

copy part from 's3://awssampledb/ssbgz/part' 
credentials 'aws_access_key_id=<AWS_ACCESS_KEY_ID>;aws_secret_access_key=<AWS_SECRET_ACCESS_KEY>' gzip;

copy supplier from 's3://awssampledb/ssbgz/supplier' 
credentials 'aws_access_key_id=<AWS_ACCESS_KEY_ID>;aws_secret_access_key=<AWS_SECRET_ACCESS_KEY>' gzip;
-- end redshift_load_compressed
