# week 1
# Filtering 1
source="buy-clicks.csv" | stats sum(price) as amt by userId | sort -amt | head 10
# Filtering 2
source="buy-clicks.csv" | stats sum(price) as amt by userId | sort -amt | head 3
| join userId type="left" [search source="user-session.csv" | stats values(platformType) as platformType by userId delim=","]
| join userId type="left" [search source="game-clicks.csv" | stats avg(isHit) as hitRatio by userId]


