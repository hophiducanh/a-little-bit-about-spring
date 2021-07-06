drop table if exists car;

create table car (
    id int(11) not null,
    model varchar(50) not null,
    year int(4) not null,
    primary key (id)
)

insert into car (model, year) values ('BMV', 2000)
insert into car (model, year) values ('BENZ', 2010)
insert into car (model, year) values ('PORCHE', 2005)
insert into car (model, year) values ('PORCHE', 2004)

----------------------------------------------------------------
----------------------------------------------------------------

DELIMITER $$

DROP PROCEDURE IF EXISTS FIND_CARS_AFTER_YEAR$$
CREATE PROCEDURE FIND_CARS_AFTER_YEAR(IN year_in INT)
BEGIN
    SELECT * FROM car WHERE year >= year_in ORDER BY year;
END$$

-----------------------------------------------------------------

DROP PROCEDURE IF EXISTS GET_TOTAL_CARS_BY_MODEL$$
CREATE PROCEDURE GET_TOTAL_CARS_BY_MODEL(IN model_in VARCHAR(50), OUT count_out INT)
BEGIN
    SELECT COUNT(*) into count_out from car WHERE model = model_in;
END$$

DELIMITER ;