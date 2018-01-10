# Storj-HostStats
https://play.google.com/store/apps/details?id=com.steinbacher.storj_hoststats_app

<img src="https://raw.githubusercontent.com/geckogecko/storj_hoststats_app/master/screenshots/joined.jpg" alt="Screenshots" height="350"/>

Monitors and analyses the following stats of your Storj-Nodes with the help of the official Storj Api:
- Online Since
- Last Seen Date
- Address
- Port
- UserAgent
- Protocol 
- Response Time
- Last Timeout 
- TimeoutRate
- Last Contract Sent
- Last Contract Sent Update Time 
- Reputation
- Space available
- Shared disk space (with [StorjDash Integration](www.storjdash.com))

Sends an alert if:

- one of your nodes went offline
- a new storj version got released

### Integrations
## StorjDash
Imports all your nodes from StorjDash to additionaly display your shared disk space. Enable this Integration under `Settings`

# FAQ

### 1 - What is the colored circle next to my node entry showing? 
<img src="https://raw.githubusercontent.com/geckogecko/storj_hoststats_app/master/screenshots/FAQ/line_example1.png" alt="line_example_online" height="100"/>
This circle shows the **response time** of the node in **seconds**.


### 2 - Why is the colored circle next to my node **red** with no response time?
<img src="https://raw.githubusercontent.com/geckogecko/storj_hoststats_app/master/screenshots/FAQ/line_example_offline.png" alt="line_example_offline" height="100"/>
This means your node is **offline**. 


### 3 - How can the app detect if a node is offline? 

The app checks the `LastSeen` field of for your node every 30min. If that field has not increased for more more than 30min a port scan on your nodes port is triggered. If this scan shows that your port is down its very likley that your node is offline and your node is marked as offline. 

### 4 - How can I edit/delete a node?
Simple longpress on that node and an edit screen opens.

### 5 - What is the `Reputation` field?

[SIP6 - Farmer Load Balancing Based on Reputation](https://github.com/Storj/sips/blob/master/sip-0006.md)



# Donate

[ѦRK] - `AXjrTYRkmZ9EcsHDQ1kw2FJEBF8qUWo4qA`
[ETH] - `0x719E65CdB86889A3CE09c5eF83f6Bd900831c6Df`


# Contact


[EMail] - georgsteinbacher@gmx.net



