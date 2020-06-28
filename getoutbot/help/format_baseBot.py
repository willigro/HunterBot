# -*- coding: utf-8 -*-

with open("baseBotUnformated.txt", "rt") as fin:
    with open("baseBot.txt", "wt") as fout:
        for line in fin:
            res = line
            
            if "/*onScannedRobot*/" in line:
                res = line.replace('/*onScannedRobot*/', '$onScannedRobot')
            if "/*doMove*/" in line:
                res = line.replace('/*doMove*/', '$doMove')
            if "GetOutBot" in line:
                res = line.replace('GetOutBot', 'GetOutBot$_id')
            
                        
            fout.write(res)