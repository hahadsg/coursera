// chat_create_team_chat.csv
// userid, teamid, TeamChatSessionID, timestamp
LOAD CSV FROM "file:/Users/hahadsg/Downloads/tmp/z/big_data_capstone_datasets_and_scripts/chat-data/chat_create_team_chat.csv" AS row
MERGE (u:User {id: toInt(row[0])}) MERGE (t:Team {id: toInt(row[1])}) 
MERGE (c:TeamChatSession {id: toInt(row[2])})
MERGE (u)-[:CreatesSession{timeStamp: row[3]}]->(c)
MERGE (c)-[:OwnedBy{timeStamp: row[3]}]->(t) 

// chat_join_team_chat.csv
// userid, TeamChatSessionID, timestamp
load csv from "file:/Users/hahadsg/Downloads/tmp/z/big_data_capstone_datasets_and_scripts/chat-data/chat_join_team_chat.csv" AS row
merge (u:User {id: toInt(row[0])})
merge (c:TeamChatSession {id: toInt(row[1])})
merge (u)-[:Join {timeStamp: row[2]}]->(c)

// chat_leave_team_chat.csv
// userid, teamchatsessionid, timestamp
load csv from "file:/Users/hahadsg/Downloads/tmp/z/big_data_capstone_datasets_and_scripts/chat-data/chat_leave_team_chat.csv" AS row
merge (u:User {id: toInt(row[0])})
merge (c:TeamChatSession {id: toInt(row[1])})
merge (u)-[:Leave {timeStamp: row[2]}]->(c)

// chat_item_team_chat.csv
// userid, teamchatsessionid, chatitemid, timestamp
load csv from "file:/Users/hahadsg/Downloads/tmp/z/big_data_capstone_datasets_and_scripts/chat-data/chat_item_team_chat.csv" AS row
merge (u:User {id: toInt(row[0])})
merge (c:TeamChatSession {id: toInt(row[1])})
merge (i:ChatItem {id: toInt(row[2])})
merge (u)-[:CreateChat {timeStamp: row[3]}]->(i)
merge (i)-[:PartOf {timeStamp: row[3]}]->(c)

// chat_mention_team_chat.csv
// ChatItem, userid, timeStamp
load csv from "file:/Users/hahadsg/Downloads/tmp/z/big_data_capstone_datasets_and_scripts/chat-data/chat_mention_team_chat.csv" AS row
merge (i:ChatItem {id: toInt(row[0])})
merge (u:User {id: toInt(row[1])})
merge (i)-[:Mentioned {timeStamp: row[2]}]->(u)

// chat_respond_team_chat.csv
// chatid1, chatid2,timestamp
load csv from "file:/Users/hahadsg/Downloads/tmp/z/big_data_capstone_datasets_and_scripts/chat-data/chat_respond_team_chat.csv" AS row
merge (i1:ChatItem {id: toInt(row[0])})
merge (i2:ChatItem {id: toInt(row[1])})
merge (i1)-[:ResponseTo {timestamp: row[2]}]->(i2)
















match p=(a)-[r:ResponseTo*]->(b)
with p, length(p) as plen
order by plen desc
return p, plen
limit 1

match p=(a)-[r:ResponseTo*]->(b)
where length(p) = 9
with p
match (u:User)-[:CreateChat]->(i:ChatItem)
where i in nodes(p)
return count(distinct u)




match (a:User)-[r:CreateChat]->(b)
return a.id, count(r) as chat_num
order by count(r) desc
limit 10


match (i:ChatItem)-[r1:PartOf]->(c:TeamChatSession)-[r2:OwnedBy]->(t)
return t.id, count(distinct i) as chat_num
order by count(distinct i) desc
limit 10


match p = (u:User)-[r1:CreateChat]->(i:ChatItem)-[r2:PartOf]->(c:TeamChatSession)-[r3:OwnedBy]->(t)
where u.id in [394, 2067, 209, 1087, 554, 1627, 999, 516, 461, 668] 
    and t.id in [82, 185, 112, 18, 194, 129, 52, 136, 146, 81]
return distinct u.id, t.id


match p = (u:User)-[r1:CreateChat]->(i:ChatItem)-[r2:PartOf]->(c:TeamChatSession)-[r3:OwnedBy]->(t)
where u.id in [394, 2067, 209]
return u, collect(distinct t)



match ()-[r:InteractsWith]-() delete r

match p = (u1:User)-[:CreateChat]->(i:ChatItem)-[:Mentioned]->(u2:User)
create unique((u1)-[:InteractsWith]->(u2))

match p = (u1:User)-[:CreateChat]->(i1:ChatItem)-[:ResponseTo]->(i2:ChatItem)<-[:CreateChat]-(u2:User)
create unique((u1)-[:InteractsWith]->(u2))

match (u1)-[r:InteractsWith]->(u1) delete r


match (u1:User)-[:InteractsWith]-(u2:User)
where u1.id in [394, 2067, 209, 1087, 554, 1627, 999, 516, 461, 668] 
with u1, collect(distinct u2.id) as neighbors
match (a), (b)
where a.id in neighbors and b.id in neighbors
with u1, length(neighbors) * (length(neighbors) - 1) as total_num
    , sum(case when (a)-[:InteractsWith]->(b) then 1 else 0 end) as link_num
with u1, link_num * 1.0 / total_num as cluster_coef, link_num, total_num
order by cluster_coef desc
return u1.id, cluster_coef, link_num, total_num


209 0.9523809523809523
554 0.9047619047619048
1087    0.8


