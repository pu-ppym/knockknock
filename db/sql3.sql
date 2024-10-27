use knockdb;


-- 멤버
drop table if exists members;
create table members(
	pkid int primary key auto_increment,
    user_id varchar(20) not null,
    user_pw varchar(20) not null,
    name varchar(50) not null,
    emergency_contact varchar(50)
) ENGINE=InnoDB;

-- 문 기록
drop table if exists door_access;
create table door_access(
	pkid int primary key auto_increment,
    fkmember int not null,
    access_timestamp timestamp DEFAULT CURRENT_TIMESTAMP,
    constraint fkmember_door foreign key(fkmember) references members(pkid)
) ENGINE=InnoDB;


-- 일정
drop table if exists schedules;
create table schedules(
	pkid int primary key auto_increment,
    fkmember int not null,
    tasks varchar(50),
    schedule_date date,
    constraint fkmember_task foreign key(fkmember) references members(pkid)
) ENGINE=InnoDB;

