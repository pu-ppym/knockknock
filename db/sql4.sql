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

-- 테스트
CREATE TABLE distances (
    pkid INT AUTO_INCREMENT PRIMARY KEY,
    distance INT NOT NULL,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
);

select * from distances;

drop table if exists schedules;
create table schedules(
	pkid int primary key auto_increment,
    fkmember int not null,
    tasks varchar(50),
    schedule_date date,
    constraint fkmember_task foreign key(fkmember) references members(pkid)
) ENGINE=InnoDB;

drop table if exists medication;
create table medication(
	pkid int primary key auto_increment,
    fkmember int not null,
    tasks varchar(50),
    schedule_date date,
    constraint fkmember_medication foreign key(fkmember) references members(pkid)
) ENGINE=InnoDB;


select * from members;
select * from schedules;
select * from door_access;

INSERT INTO schedules (fkmember, tasks, schedule_date) 
VALUES (5, '할일1', '2024-10-30');
INSERT INTO schedules (fkmember, tasks, schedule_date) 
VALUES (5, '할일2', '2024/10/30');

update members SET name = "test3", emergency_contact = "01033333333" where pkid = 6;

select tasks from schedules where (fkmember = 5) and (schedule_date = "2024-10-31");