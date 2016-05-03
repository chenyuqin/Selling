-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema selling
-- -----------------------------------------------------
-- best selling system ever

-- -----------------------------------------------------
-- Schema selling
--
-- best selling system ever
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `selling` DEFAULT CHARACTER SET utf8 ;
USE `selling` ;

-- -----------------------------------------------------
-- Table `selling`.`agent`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `selling`.`agent` ;

CREATE TABLE IF NOT EXISTS `selling`.`agent` (
  `agent_id` VARCHAR(20) NOT NULL,
  `upper_agent_id` VARCHAR(20) NULL,
  `agent_name` VARCHAR(45) NOT NULL,
  `agent_gender` VARCHAR(10) NOT NULL,
  `agent_phone` VARCHAR(45) NOT NULL,
  `agent_address` VARCHAR(100) NOT NULL,
  `agent_password` VARCHAR(50) NOT NULL,
  `agent_wechat` VARCHAR(45) NULL,
  `agent_level` INT NULL,
  `agent_paid` TINYINT(1) NOT NULL DEFAULT 0,
  `agent_granted` TINYINT(1) NOT NULL DEFAULT 0,
  `block_flag` TINYINT(1) NOT NULL DEFAULT 0,
  `create_time` DATETIME NOT NULL,
  PRIMARY KEY (`agent_id`))
  ENGINE = InnoDB;

LOCK TABLES `agent` WRITE;
/*!40000 ALTER TABLE `agent` DISABLE KEYS */;
INSERT INTO `agent` VALUES ('AGTyzfryl36',NULL,'章许帆','M','15895862305','江苏省南京市鼓楼区汉口路22号','Cl1z+N+m7H4FeMDP4jiCVQ==',NULL,NULL,0,0,0,'2016-04-21 16:30:05');
/*!40000 ALTER TABLE `agent` ENABLE KEYS */;
UNLOCK TABLES;


-- -----------------------------------------------------
-- Table `selling`.`customer`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `selling`.`customer` ;

CREATE TABLE IF NOT EXISTS `selling`.`customer` (
  `customer_id` VARCHAR(20) NOT NULL,
  `customer_name` VARCHAR(45) NOT NULL,
  `customer_phone` VARCHAR(45) NOT NULL,
  `customer_address` VARCHAR(100) NOT NULL,
  `agent_id` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`customer_id`),
  INDEX `fk_customer_agent1_idx` (`agent_id` ASC),
  CONSTRAINT `fk_customer_agent1`
  FOREIGN KEY (`agent_id`)
  REFERENCES `selling`.`agent` (`agent_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `selling`.`goods`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `selling`.`goods` ;

CREATE TABLE IF NOT EXISTS `selling`.`goods` (
  `goods_id` VARCHAR(20) NOT NULL,
  `goods_name` VARCHAR(45) NOT NULL,
  `goods_price` DOUBLE NOT NULL,
  `goods_description` VARCHAR(45) NULL,
  `block_flag` TINYINT(1) NOT NULL DEFAULT 0,
  `create_time` DATETIME NOT NULL,
  PRIMARY KEY (`goods_id`))
  ENGINE = InnoDB;

LOCK TABLES `goods` WRITE;
/*!40000 ALTER TABLE `goods` DISABLE KEYS */;
INSERT INTO `goods` VALUES ('COMofrefl41','三七粉',298,'100g',0,'2016-04-29 14:23:08');
/*!40000 ALTER TABLE `goods` ENABLE KEYS */;
UNLOCK TABLES;

-- -----------------------------------------------------
-- Table `selling`.`order`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `selling`.`order` ;

CREATE TABLE IF NOT EXISTS `selling`.`order` (
  `order_id` VARCHAR(20) NOT NULL,
  `agent_id` VARCHAR(20) NOT NULL,
  `create_time` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`order_id`),
  INDEX `fk_order_agent1_idx` (`agent_id` ASC),
  CONSTRAINT `fk_order_agent1`
  FOREIGN KEY (`agent_id`)
  REFERENCES `selling`.`agent` (`agent_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `selling`.`order_item`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `selling`.`order_item` ;

CREATE TABLE IF NOT EXISTS `selling`.`order_item` (
  `order_item_id` VARCHAR(20) NOT NULL,
  `order_id` VARCHAR(20) NOT NULL,
  `goods_id` VARCHAR(20) NOT NULL,
  `goods_quantity` INT NOT NULL DEFAULT 1,
  `order_item_price` DOUBLE NOT NULL,
  `customer_id` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`order_item_id`),
  INDEX `fk_order_item_order_idx` (`order_id` ASC),
  INDEX `fk_order_item_goods1_idx` (`goods_id` ASC),
  INDEX `fk_order_item_customer1_idx` (`customer_id` ASC),
  CONSTRAINT `fk_order_item_order`
  FOREIGN KEY (`order_id`)
  REFERENCES `selling`.`order` (`order_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_order_item_goods1`
  FOREIGN KEY (`goods_id`)
  REFERENCES `selling`.`goods` (`goods_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_order_item_customer1`
  FOREIGN KEY (`customer_id`)
  REFERENCES `selling`.`customer` (`customer_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `selling`.`manager`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `selling`.`manager` ;

CREATE TABLE IF NOT EXISTS `selling`.`manager` (
  `manager_id` VARCHAR(20) NOT NULL,
  `manager_username` VARCHAR(45) NOT NULL,
  `manager_password` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`manager_id`))
  ENGINE = InnoDB;

LOCK TABLES `manager` WRITE;
/*!40000 ALTER TABLE `manager` DISABLE KEYS */;
INSERT INTO `manager` VALUES ('MNG000001','admin','21232f297a57a5a743894a0e4a801fc3');
/*!40000 ALTER TABLE `manager` ENABLE KEYS */;
UNLOCK TABLES;


-- -----------------------------------------------------
-- Table `selling`.`role`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `selling`.`role` ;

CREATE TABLE IF NOT EXISTS `selling`.`role` (
  `role_id` VARCHAR(20) NOT NULL,
  `role_name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`role_id`))
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `selling`.`user`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `selling`.`user` ;

CREATE TABLE IF NOT EXISTS `selling`.`user` (
  `userid` VARCHAR(20) NOT NULL,
  `username` VARCHAR(45) NOT NULL,
  `password` VARCHAR(45) NOT NULL,
  `role_id` VARCHAR(20) NOT NULL,
  `manager_id` VARCHAR(20) NULL,
  `agent_id` VARCHAR(20) NULL,
  `block_flag` TINYINT(1) NOT NULL DEFAULT 0,
  `create_time` DATETIME NOT NULL,
  PRIMARY KEY (`userid`),
  INDEX `fk_user_role1_idx` (`role_id` ASC),
  INDEX `fk_user_agent1_idx` (`agent_id` ASC),
  INDEX `fk_user_manager1_idx` (`manager_id` ASC),
  CONSTRAINT `fk_user_role1`
  FOREIGN KEY (`role_id`)
  REFERENCES `selling`.`role` (`role_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_agent1`
  FOREIGN KEY (`agent_id`)
  REFERENCES `selling`.`agent` (`agent_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_manager1`
  FOREIGN KEY (`manager_id`)
  REFERENCES `selling`.`manager` (`manager_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
