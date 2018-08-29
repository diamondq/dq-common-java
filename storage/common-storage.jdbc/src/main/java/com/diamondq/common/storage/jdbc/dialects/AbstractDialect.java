package com.diamondq.common.storage.jdbc.dialects;

import com.diamondq.common.storage.jdbc.IJDBCDialect;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class AbstractDialect implements IJDBCDialect {

  protected static Set<@NonNull String> sSQL_2003_RESERVED_WORDS;

  static {
    String wordsStr = "add,all,allocate,alter,and,any,are,array,as,asensitive,asymmetric,at,atomic,authorization,"
      + "begin,between,bigint,binary,blob,boolean,both,by,"
      + "call,called,cascaded,case,cast,char,character,check,clob,close,collate,column,commit,condition,connect,constraint,continue,corresponding,create,cross,cube,current,current_date,current_default_transform_group,current_path,current_role,current_time,current_timestamp,current_transform_group_for_type,current_user,cursor,cycle,"
      + "date,day,deallocate,dec,decimal,declare,default,delete,deref,describe,deterministic,disconnect,distinct,do,double,drop,dynamic,"
      + "each,element,else,elseif,end,escape,except,exec,execute,exists,exit,external,"
      + "false,fetch,filter,float,for,foreign,free,from,full,function,"
      + "get,global,grant,group,grouping,handler,having,hold,hour,"
      + "identity,if,immediate,in,indicator,inner,inout,input,insensitive,insert,int,integer,intersect,interval,into,is,iterate,"
      + "join,language,large,lateral,leading,leave,left,like,local,localtime,localtimestamp,loop,"
      + "match,member,merge,method,minute,modifies,module,month,multiset,"
      + "national,natural,nchar,nclob,new,no,none,not,null,numeric,"
      + "of,old,on,only,open,or,order,out,outer,output,over,overlaps,"
      + "parameter,partition,precision,prepare,primary,procedure,"
      + "range,reads,real,recursive,ref,references,referencing,release,repeat,resignal,result,return,returns,revoke,right,rollback,rollup,row,rows,"
      + "savepoint,scope,scroll,search,second,select,sensitive,session_user,set,signal,similar,smallint,some,specific,specifictype,sql,sqlexception,sqlstate,sqlwarning,start,static,submultiset,symmetric,system,system_user,"
      + "table,tablesample,then,time,timestamp,timezone_hour,timezone_minute,to,trailing,translation,treat,trigger,true,"
      + "undo,union,unique,unknown,unnest,until,update,user,using,"
      + "value,values,varchar,varying,when,whenever,where,while,window,with,within,without,year";
    @NonNull
    String @NonNull [] words = wordsStr.split(",");
    ImmutableSet.Builder<@NonNull String> builder = ImmutableSet.builder();
    for (String w : words)
      builder.add(w);
    sSQL_2003_RESERVED_WORDS = builder.build();
  }

}
