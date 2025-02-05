package hlf.java.rest.client.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.impl.GatewayImpl;
import org.hyperledger.fabric.protos.common.Configtx.Config;
import org.hyperledger.fabric.protos.common.Configtx.ConfigGroup;
import org.hyperledger.fabric.protos.common.Configtx.ConfigUpdate;
import org.hyperledger.fabric.protos.common.Configtx.ConfigUpdate.Builder;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.UpdateChannelConfiguration;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import com.google.protobuf.util.JsonFormat.Parser;
import com.google.protobuf.util.JsonFormat.Printer;

import hlf.java.rest.client.exception.ErrorCode;
import hlf.java.rest.client.exception.ErrorConstants;
import hlf.java.rest.client.model.ClientResponseModel;
import hlf.java.rest.client.model.CommitChannelParamsDTO;
import hlf.java.rest.client.model.NewOrgParamsDTO;
import hlf.java.rest.client.service.AddOrgToChannelWriteSetBuilder;
import hlf.java.rest.client.service.ChannelConfigDeserialization;

@ExtendWith(MockitoExtension.class)
public class NetworkStatusImplTest {
  
  @InjectMocks
  private NetworkStatusImpl networkStatus;
  
  @Mock
  private GatewayImpl gateway;
  
  @Mock 
  private Network network;
   
  @Mock 
  private Channel channel;
  
  @Mock
  private Config channelConfig;
  
  @Mock
  private MockedStatic<Config>  staticConfig;
  
  @Mock
  private MockedStatic<JsonFormat> staticJsonFormat;
  
  @Mock
  private Printer printer;
  
  @Mock
  private AddOrgToChannelWriteSetBuilder addOrgToChannelWriteSetBuilder;

  @Mock
  private ChannelConfigDeserialization channelConfigDeserialization;
  
  @Mock
  private MockedStatic<ConfigUpdate> staticConfigUpdate;
  
  @Mock
  private ConfigUpdate configUpdate;
  
  @Mock
  private ConfigGroup readset; 
  
  @Mock
  private ConfigGroup writeset; 
  
  @Mock
  private Builder builder;
  
  @Mock
  private User client;
  
  @Mock
  private HFClient hfClient;
  
  @Mock
  private Parser parser;
  
  @Mock
  private UpdateChannelConfiguration updateChannelConfiguration;
  
  @Mock
  private ByteString byteString;
  
  @Test
  public void getChannelFromNetworkTest() throws InvalidProtocolBufferException, InvalidArgumentException, TransactionException {
    ResponseEntity<ClientResponseModel> responseEntity = new ResponseEntity<>(new ClientResponseModel(ErrorConstants.NO_ERROR, "dGhlX2NvbmZpZw=="),
        HttpStatus.OK);
    Mockito.when(gateway.getNetwork(Mockito.anyString())).thenReturn(network);
    Mockito.when(network.getChannel()).thenReturn(channel);
    staticConfig.when(()-> Config.parseFrom(Mockito.any(byte[].class))).thenReturn(channelConfig);
    Mockito.when(channel.getChannelConfigurationBytes()).thenReturn(new byte[0]);
    staticJsonFormat.when(JsonFormat::printer).thenReturn(printer);
    Mockito.when(printer.print(Mockito.any(MessageOrBuilder.class))).thenReturn("the_config");
    assertEquals(responseEntity.getBody().getMessage(), networkStatus.getChannelFromNetwork("some_channelname").getBody().getMessage());
  }
  
  @Test
  public void generateConfigUpdateTest() throws InvalidArgumentException, TransactionException, InvalidProtocolBufferException {
    ResponseEntity<ClientResponseModel> responseEntity = new ResponseEntity<>(new ClientResponseModel(ErrorConstants.NO_ERROR, "dGhlX2NvbmZpZw=="),
        HttpStatus.OK);
    Mockito.when(gateway.getNetwork(Mockito.anyString())).thenReturn(network);
    Mockito.when(network.getChannel()).thenReturn(channel);
    staticConfigUpdate.when(()-> ConfigUpdate.parseFrom(Mockito.any(byte[].class))).thenReturn(configUpdate);
    Mockito.when(channel.getChannelConfigurationBytes()).thenReturn(new byte[0]);
    staticConfigUpdate.when(ConfigUpdate::newBuilder).thenReturn(builder);
    Mockito.when(builder.setChannelId(Mockito.anyString())).thenReturn(builder);
    Mockito.when(builder.setReadSet(Mockito.any(ConfigGroup.class))).thenReturn(builder);
    Mockito.when(builder.setWriteSet(Mockito.any(ConfigGroup.class))).thenReturn(builder);
    Mockito.when(builder.build()).thenReturn(configUpdate);
    Mockito.when(configUpdate.getReadSet()).thenReturn(readset);
    Mockito.when(addOrgToChannelWriteSetBuilder.buildWriteset(Mockito.any(ConfigGroup.class),Mockito.any(NewOrgParamsDTO.class))).thenReturn(writeset);
    staticJsonFormat.when(JsonFormat::printer).thenReturn(printer);
    Mockito.when(printer.print(Mockito.any(MessageOrBuilder.class))).thenReturn("the_config");
    assertEquals(responseEntity.getBody().getMessage(), networkStatus.generateConfigUpdate("some_channelname", new NewOrgParamsDTO()).getBody().getMessage());
  }
  
  @Test
  public void signChannelConfigTransactionTest() throws InvalidArgumentException {
    byte[] outputByteArray = new byte[0];
    ResponseEntity<ClientResponseModel> responseEntity = new ResponseEntity<>(new ClientResponseModel(ErrorConstants.NO_ERROR, outputByteArray),
        HttpStatus.OK);
    Mockito.when(gateway.getNetwork(Mockito.anyString())).thenReturn(network);
    Mockito.when(gateway.getClient()).thenReturn(hfClient);
    Mockito.when(hfClient.getUserContext()).thenReturn(client);
    Mockito.when(network.getChannel()).thenReturn(channel);
    staticConfigUpdate.when(()-> ConfigUpdate.newBuilder()).thenReturn(builder);
    staticJsonFormat.when(JsonFormat::parser).thenReturn(parser);    
    Mockito.when(builder.build()).thenReturn(configUpdate);
    Mockito.when(configUpdate.toByteString()).thenReturn(byteString);
    Mockito.when(byteString.toByteArray()).thenReturn(new byte[1]);
    Mockito.when(builder.build()).thenReturn(configUpdate);
    Mockito.when(channel.getUpdateChannelConfigurationSignature(Mockito.any(UpdateChannelConfiguration.class), Mockito.any(User.class))).thenReturn(outputByteArray);
    assertEquals(responseEntity.getBody().getMessage(), networkStatus.signChannelConfigTransaction("some_channel_name","ConfigUpdateEncodedFile").getBody().getMessage());
  }
  
  @Test
  public void commitChannelConfigTransactionTest() throws InvalidArgumentException {
    List<byte[]> byteArrayList = new ArrayList<byte[]>();
    byte[] outputByteArray = new byte[0];
    byteArrayList.add(outputByteArray);
    CommitChannelParamsDTO commitChannelParamsDTO = new CommitChannelParamsDTO();
    commitChannelParamsDTO.setSignatures(byteArrayList);
    commitChannelParamsDTO.setConfigUpdateBase64Encoded("ConfigUpdateEncodedFile");
    ResponseEntity<ClientResponseModel> responseEntity = new ResponseEntity<>(new ClientResponseModel(ErrorConstants.NO_ERROR, ErrorCode.SUCCESS.getReason()),
        HttpStatus.OK);
    Mockito.when(gateway.getNetwork(Mockito.anyString())).thenReturn(network);
    Mockito.when(gateway.getClient()).thenReturn(hfClient);
    Mockito.when(hfClient.getUserContext()).thenReturn(client);
    Mockito.when(network.getChannel()).thenReturn(channel);
    staticConfigUpdate.when(()-> ConfigUpdate.newBuilder()).thenReturn(builder);
    staticJsonFormat.when(JsonFormat::parser).thenReturn(parser);    
    Mockito.when(builder.build()).thenReturn(configUpdate);
    Mockito.when(configUpdate.toByteString()).thenReturn(byteString);
    Mockito.when(byteString.toByteArray()).thenReturn(new byte[1]);
    Mockito.when(builder.build()).thenReturn(configUpdate);
    assertEquals(responseEntity.getBody().getMessage(), networkStatus.commitChannelConfigTransaction("some_channel_name", commitChannelParamsDTO).getBody().getMessage());
  }
  
  @Test
  public void addOrgToChannelTest() throws InvalidArgumentException, TransactionException, InvalidProtocolBufferException {
    byte[] outputByteArray = new byte[0];
    ResponseEntity<ClientResponseModel> responseEntity = new ResponseEntity<>(new ClientResponseModel(ErrorConstants.NO_ERROR, ErrorCode.SUCCESS.getReason()),
        HttpStatus.OK);
    Mockito.when(gateway.getNetwork(Mockito.anyString())).thenReturn(network);
    Mockito.when(gateway.getClient()).thenReturn(hfClient);
    Mockito.when(hfClient.getUserContext()).thenReturn(client);
    Mockito.when(network.getChannel()).thenReturn(channel);
    Mockito.when(channel.getChannelConfigurationBytes()).thenReturn(new byte[0]);
    staticConfigUpdate.when(()-> ConfigUpdate.parseFrom(Mockito.any(byte[].class))).thenReturn(configUpdate);
    staticConfigUpdate.when(()-> ConfigUpdate.newBuilder()).thenReturn(builder);
    staticJsonFormat.when(JsonFormat::printer).thenReturn(printer);
    Mockito.when(printer.print(Mockito.any(MessageOrBuilder.class))).thenReturn("the_config");
    staticConfigUpdate.when(ConfigUpdate::newBuilder).thenReturn(builder);
    Mockito.when(configUpdate.getReadSet()).thenReturn(readset);
    Mockito.when(builder.setChannelId(Mockito.anyString())).thenReturn(builder);
    Mockito.when(builder.setReadSet(Mockito.any(ConfigGroup.class))).thenReturn(builder);
    Mockito.when(addOrgToChannelWriteSetBuilder.buildWriteset(Mockito.any(ConfigGroup.class),Mockito.any(NewOrgParamsDTO.class))).thenReturn(writeset);
    Mockito.when(builder.setWriteSet(Mockito.any(ConfigGroup.class))).thenReturn(builder);
    Mockito.when(builder.build()).thenReturn(configUpdate);
    Mockito.when(configUpdate.toByteString()).thenReturn(byteString);
    Mockito.when(byteString.toByteArray()).thenReturn(new byte[1]);
    Mockito.when(builder.build()).thenReturn(configUpdate);
    Mockito.when(channel.getUpdateChannelConfigurationSignature(Mockito.any(UpdateChannelConfiguration.class), Mockito.any(User.class))).thenReturn(outputByteArray);
    assertEquals(responseEntity.getBody().getMessage(), networkStatus.addOrgToChannel("some_channel_name", new NewOrgParamsDTO()).getBody().getMessage());
  }
}
