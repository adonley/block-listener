DROP TABLE IF EXISTS `ethereum_address`;
CREATE TABLE `ethereum_address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL UNIQUE,
  `decimal_places` int(11) DEFAULT NULL,
  `icon_url` varchar(255) DEFAULT NULL,
  `is_contract` bit(1) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `symbol` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `ethereum_address`
--

LOCK TABLES `ethereum_address` WRITE;
/*!40000 ALTER TABLE `ethereum_address` DISABLE KEYS */;
/*!40000 ALTER TABLE `ethereum_address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ethereum_transaction`
--

DROP TABLE IF EXISTS `ethereum_transaction`;
CREATE TABLE `ethereum_transaction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `block_number` bigint(20) DEFAULT NULL,
  `time` datetime DEFAULT NULL,
  `transaction_hash` varchar(255) DEFAULT NULL,
  `transaction_type` varchar(255) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  `ethereum_contract` bigint(20) DEFAULT NULL,
  `from_address` bigint(20) DEFAULT NULL,
  `to_address` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `ethereum_transaction`
--

LOCK TABLES `ethereum_transaction` WRITE;
/*!40000 ALTER TABLE `ethereum_transaction` DISABLE KEYS */;
/*!40000 ALTER TABLE `ethereum_transaction` ENABLE KEYS */;
UNLOCK TABLES;
