SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';


-- -----------------------------------------------------
-- Table `local_map`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `local_map` ;

CREATE  TABLE IF NOT EXISTS `local_map` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `primary_id` VARCHAR(255) NOT NULL ,
  `local_context` VARCHAR(255) NOT NULL ,
  `local_id` VARCHAR(255) NOT NULL ,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY (`id`) ,
  UNIQUE KEY `ix_unique` ( `local_context` ,`local_id`),
  KEY `created` (`created` ASC) ,
  KEY `primary_id` (`primary_id` ASC) ,
  KEY `local_context` (`local_context` ASC) ,
  KEY `local_id` (`local_id` ASC) )
ENGINE = InnoDB
ROW_FORMAT = DYNAMIC;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
