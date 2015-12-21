import sys

NAME_FLAG = "column=data:title"
SPLIT_FLAG = "value="
for line in sys.stdin:
        if NAME_FLAG in line:
                print line.split(SPLIT_FLAG)[1].strip()
