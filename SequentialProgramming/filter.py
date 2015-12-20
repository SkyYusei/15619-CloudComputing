import re
file=open("pagecounts-20150801-000000","r")
openfile=open("temp","w")
num = 0
for line in file:
	if(re.search(r"^en ",line)): #rule 1
		if(re.search(r"^en (Media:|Special:|Talk:|User:|User_talk:|Project:|Project_talk:|File:|File_talk:|MediaWiki:|MediaWiki_talk:|Template:|Template_talk:|Help:|Help_talk:|Category:|Category_talk:|Portal:|Wikipedia:|Wikipedia_talk:)",line) == None): #rule 2
			if(re.search(r"^en [a-z]{1,}",line)== None): #rule 3
					if(re.search(r".*(\.jpg|\.gif|\.png|\.JPG|\.GIF|\.PNG|\.txt|\.ico) [0-9]+ [0-9]+$",line)== None): #rule 4
						if(re.search(r".* (404_error/|Main_Page|Hypertext_Transfer_Protocol|Search) [0-9]+ [0-9]+$",line)== None):#rule 5
							a=line.split(" ")#rule 6
							openfile.write(a[1]+'\t'+a[2]+'\n')
print num

