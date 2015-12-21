#use to prepare data for DynamDB
import json
readfile = open("test.json","r")
writefile = open("postdone","w")
for line in readfile:
	json_t = json.loads(line)
	uid = {}
	time = {}
	post = {}
	uid['n'] = str(json_t['uid'])
	time['s'] = json_t['timestamp']
	post['s'] = json.dumps(json_t)
	# print chr(2)+["uid"]
	writefile.write('UserID'+chr(3)+json.dumps(uid,separators=(',', ':'))+chr(2)+'Timestamp'+chr(3)+json.dumps(time,separators=(',', ':'))+chr(2)+'Post'+chr(3)+json.dumps(post,separators=(',', ':')))
	writefile.write('\n')

