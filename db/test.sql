
insert into folder(id,title,parentid,leaf,status,inserttime,operatetime,deletetime)
values(id_seq.nextVal,'目录1',null,'0','0',CURRENT_TIMESTAMP(0),CURRENT_TIMESTAMP(0),null);


drop table todo;
drop table article;
drop table folder;
drop table timeline;

alter table todo alter column inserttime type timestamp;
alter table todo alter column operatetime type timestamp;
alter table todo alter column deletetime type timestamp;

alter table article alter column inserttime type timestamp;
alter table article alter column operatetime type timestamp;
alter table article alter column deletetime type timestamp;

alter table folder alter column inserttime type timestamp;
alter table folder alter column operatetime type timestamp;
alter table folder alter column deletetime type timestamp;

alter table timeline alter column inserttime type timestamp;
alter table timeline alter column operatetime type timestamp;
