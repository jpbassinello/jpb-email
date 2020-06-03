# jpb-email
Sending email from my projects

## database ##

Create `EMAIL` table

```sql
CREATE TABLE `EMAIL` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `EMAIL` varchar(255) NOT NULL,
  `NAME` varchar(255) NOT NULL,
  `SUBJECT` varchar(255) NOT NULL,
  `BODY` varchar(10000) NOT NULL,
  `SENT_DATE_TIME` datetime DEFAULT NULL,
  `CREATED_DATE_TIME` datetime NOT NULL,
  `TRIES` int(11) NOT NULL DEFAULT '0',
  `SENT` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
);
```
