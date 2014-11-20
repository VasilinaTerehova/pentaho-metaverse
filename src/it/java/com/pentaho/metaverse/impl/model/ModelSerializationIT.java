/*
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2002 - 2014 Pentaho Corporation (Pentaho). All rights reserved.
 *
 * NOTICE: All information including source code contained herein is, and
 * remains the sole property of Pentaho and its licensors. The intellectual
 * and technical concepts contained herein are proprietary and confidential
 * to, and are trade secrets of Pentaho and may be covered by U.S. and foreign
 * patents, or patents in process, and are protected by trade secret and
 * copyright laws. The receipt or possession of this source code and/or related
 * information does not convey or imply any rights to reproduce, disclose or
 * distribute its contents, or to manufacture, use, or sell anything that it
 * may describe, in whole or in part. Any reproduction, modification, distribution,
 * or public display of this information without the express written authorization
 * from Pentaho is strictly prohibited and in violation of applicable laws and
 * international treaties. Access to the source code contained herein is strictly
 * prohibited to anyone except those individuals and entities who have executed
 * confidentiality and non-disclosure agreements or other agreements with Pentaho,
 * explicitly covering such access.
 */

package com.pentaho.metaverse.impl.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.sql.Timestamp;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ModelSerializationIT {

  ObjectMapper mapper;

  @Before
  public void setUp() throws Exception {
    mapper = new ObjectMapper();
    mapper.enable( SerializationFeature.INDENT_OUTPUT );
    mapper.disable( SerializationFeature.FAIL_ON_EMPTY_BEANS );
    mapper.enable( SerializationFeature.WRAP_EXCEPTIONS );
  }

  @Test
  public void testSerializeDeserialize() throws Exception {

    String server = "localhost";
    String dbName = "test";
    int port = 9999;
    String user = "testUser";
    String password = "password";

    JdbcResourceInfo jdbcResource = new JdbcResourceInfo( server, dbName, port, user, password );
    jdbcResource.setInput( true );

    String json = mapper.writeValueAsString( jdbcResource );
    System.out.println( json );

    JdbcResourceInfo rehydrated = mapper.readValue( json, JdbcResourceInfo.class );

    assertEquals( jdbcResource.getServer(), rehydrated.getServer() );
    assertEquals( jdbcResource.getDatabaseName(), rehydrated.getDatabaseName() );
    assertEquals( jdbcResource.getUsername(), rehydrated.getUsername() );
    assertEquals( jdbcResource.getPassword(), rehydrated.getPassword() );
    assertEquals( jdbcResource.getPort(), rehydrated.getPort() );
    assertEquals( jdbcResource.isInput(), rehydrated.isInput() );
    
    ExecutionProfile executionProfile = new ExecutionProfile("run1", "some/path/to/a.ktl", "tranformation", "A test profile");

    long currentMillis = System.currentTimeMillis();
    long futureMillis = currentMillis + 10000;
    Timestamp startTime = new Timestamp(currentMillis);
    Timestamp endTime = new Timestamp(futureMillis);    
    executionProfile.getExecutionData().setStartTime( startTime );
    executionProfile.getExecutionData().setEndTime( endTime );
    executionProfile.getExecutionData().setClientExecuter( "client.executer" );
    executionProfile.getExecutionData().setExecutingServer( "www.pentaho.com" );
    executionProfile.getExecutionData().setExecutingUser( "wseyler" );
    executionProfile.getExecutionData().setLoggingChannelId( "kettle.debug" );
    executionProfile.getExecutionData().addParameter( new ParamInfo( "testParam1", "Larry", "Fine", "A Test Parameter" ) );
    executionProfile.getExecutionData().addParameter( new ParamInfo( "testParam2", "Howard",  "Moe", "Another Parameter" ) );
    executionProfile.getExecutionData().addParameter( new ParamInfo( "testParam3", "Fine", "Curly", "A Third Parameter") );
    BaseResourceInfo externalResourceInfo = new BaseResourceInfo();
    externalResourceInfo.setDescription( "A test csv file" );
    externalResourceInfo.setInput( true );
    externalResourceInfo.setName( "prices.csv" );
    externalResourceInfo.setType( "csv" );
    externalResourceInfo.putAttribute( "hair", "red");
    executionProfile.getExecutionData().addExternalResource( externalResourceInfo );
    
    json = mapper.writeValueAsString( executionProfile );
    System.out.println( json );
    
    ExecutionProfile rehydratedProfile = mapper.readValue( json, ExecutionProfile.class );
    assertEquals( executionProfile.getName(), rehydratedProfile.getName() );
    assertEquals( executionProfile.getPath(), rehydratedProfile.getPath() );
    assertEquals( executionProfile.getType(), rehydratedProfile.getType() );
    assertEquals( executionProfile.getDescription(), rehydratedProfile.getDescription() );
    assertEquals( executionProfile.getExecutionData().getStartTime().compareTo( rehydratedProfile.getExecutionData().getStartTime()), 0 );
    assertEquals( executionProfile.getExecutionData().getEndTime().compareTo( rehydratedProfile.getExecutionData().getEndTime()), 0 ) ;
    assertEquals( executionProfile.getExecutionData().getFailureCount(), 0);
    assertEquals( executionProfile.getExecutionData().getClientExecuter(), rehydratedProfile.getExecutionData().getClientExecuter());
    assertEquals( executionProfile.getExecutionData().getExecutingServer(), rehydratedProfile.getExecutionData().getExecutingServer());
    assertEquals( executionProfile.getExecutionData().getExecutingUser(), rehydratedProfile.getExecutionData().getExecutingUser());
    assertEquals( executionProfile.getExecutionData().getLoggingChannelId(), rehydratedProfile.getExecutionData().getLoggingChannelId() );
    assertEquals( rehydratedProfile.getExecutionData().getParameters().size(), 3);
  }
}
