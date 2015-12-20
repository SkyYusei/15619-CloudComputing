#!/usr/bin/env python
import re
import os
import sys



#filename = ["pagecounts-20150801-000000","pagecounts-20150801-010000","pagecounts-20150801-020000","pagecounts-20150802-010000","pagecounts-20150802-000000","pagecounts-20150803-000000"]
#openfile=open("output","w")
#for fname in filename:


for line in sys.stdin:
	cline=line.split(" ")
	if(re.search(r"^en$",cline[0])): #rule 1
		if(re.search(r"^(Media:|Special:|Talk:|User:|User_talk:|Project:|Project_talk:|File:|File_talk:|MediaWiki:|MediaWiki_talk:|Template:|Template_talk:|Help:|Help_talk:|Category:|Category_talk:|Portal:|Wikipedia:|Wikipedia_talk:)",cline[1]) == None): #rule 2
			if(re.search(r"^[a-z]{1,}",cline[1])== None): #rule 3
				if(re.search(r"(\.jpg|\.gif|\.png|\.JPG|\.GIF|\.PNG|\.txt|\.ico)$",cline[1])== None): #rule 4
					if(re.search(r"^(404_error/|Main_Page|Hypertext_Transfer_Protocol|Search)$",cline[1])== None):#rule 5
						a=line.split(" ")#rule 6
						if (a[1]):
							fname = os.environ["mapreduce_map_input_file"]
							date = fname.split("-")[2]
							print"%s\t%s\t%s"%(a[1],date,a[2])
							



