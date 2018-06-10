package com.diamondq.common.storage.jdbc.osgi;

import com.diamondq.common.injection.osgi.AbstractOSGiConstructor;
import com.diamondq.common.injection.osgi.ConstructorInfoBuilder;
import com.diamondq.common.storage.jdbc.IJDBCDialect;
import com.diamondq.common.storage.jdbc.JDBCKVStore;
import com.diamondq.common.storage.kv.IKVStore;

import javax.sql.DataSource;

public class OSGiJDBCKVStoreBuilder extends AbstractOSGiConstructor {

	public OSGiJDBCKVStoreBuilder() {
		// public JDBCKVStore(DataSource pDatabase, IJDBCDialect pDialect, @Nullable String pTableSchema) {
		super(ConstructorInfoBuilder.builder() //
			.register(IKVStore.class) //
			.constructorClass(JDBCKVStore.class) //
			.cArg().type(DataSource.class).propFilter(".datasource_filter").required().build() //
			.cArg().type(IJDBCDialect.class).propFilter(".dialect_filter").required().build() //
			.cArg().type(String.class).prop(".tableSchema").optional().build());
	}

}
