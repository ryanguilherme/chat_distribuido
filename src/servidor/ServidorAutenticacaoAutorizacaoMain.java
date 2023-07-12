package servidor;

import protocolo.ProtocoloRequisicao;
import protocolo.ProtocoloResposta;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.ProtocolStringList;

import mensagem.MensagemProtocolBuffers.Mensagem;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import redis.clients.jedis.*;

public class ServidorAutenticacaoAutorizacaoMain {

    public static void main(String[] args) {
        Socket socket = null;
        Socket socket2 = null;

        // Configure the SSLContext
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            try {
                trustManagerFactory.init((KeyStore) null);
                TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
                sslContext.init(null, trustManagers, null);
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }

        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
            return;
        }

        try {
            // Configure the JedisPoolConfig
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMinIdle(10);
            poolConfig.setMaxIdle(50);

            // Configure the JedisPool connection using SSL/TLS
            JedisPool jedisPool = new JedisPool(poolConfig, "172.25.240.241", 6379, 2000, 0, 0, null, 0, null, false,
                    sslContext.getSocketFactory(), null, null);
            BinaryJedis jedis = jedisPool.getResource();
            BinaryJedis jedis2 = jedisPool.getResource();

            socket = new Socket("localhost", 6770);
            socket2 = new Socket("localhost", 6780);

            InputStream in;
            OutputStream out;
            OutputStream out2;
            String canalRequisicao = "requisicao";
            String canalResposta = "resposta";

            in = socket.getInputStream();
            out = socket.getOutputStream();
            out2 = socket2.getOutputStream();

            BinaryJedisPubSub jedisPubSub = new BinaryJedisPubSub() {

                public void onSubscribe(byte[] channel, int subscribedChannels) {
                    System.out.println("Inscrito no canal" + channel.toString());
                }

                @Override
                public void onMessage(byte[] channel, byte[] message) {

                    if (message != null && message.length > 0) {

                        // Handle received message from 'requisicao' channel
                        byte[] mensagem = message;

                        Mensagem mensagemConvertida;
                        try {
                            mensagemConvertida = Mensagem.parseFrom(mensagem);
                            ProtocolStringList parametrosList = mensagemConvertida.getParametrosList();
                            String[] parametros = parametrosList.toArray(new String[parametrosList.size()]);
                            List<String> parametrosLista = Arrays.asList(parametros);

                            Mensagem requisicao = null;

                            System.out.println(mensagemConvertida.getCodOperacao());

                            if (mensagemConvertida.getCodOperacao() == ProtocoloRequisicao.OP_CADASTRAR_USUARIO) {
                                out.write(mensagemConvertida.toByteArray());
                                out.flush();

                                byte[] resposta = new byte[4096];
                                int bytesRead = in.read(resposta, 0, resposta.length);
                                byte[] respostaData = Arrays.copyOf(resposta, bytesRead);
                                jedis2.publish(canalResposta.getBytes(), respostaData);
                            } else if (mensagemConvertida.getCodOperacao() == ProtocoloRequisicao.OP_LOGAR_USUARIO) {
                                System.out.println("aqui");
                                out.write(mensagemConvertida.toByteArray());
                                out.flush();
                                byte[] resposta = new byte[4096];
                                int bytesRead = in.read(resposta, 0, resposta.length);
                                byte[] respostaData = Arrays.copyOf(resposta, bytesRead);
                                jedis2.publish(canalResposta.getBytes(), respostaData);
                            } else {
                                Mensagem.Builder requisicaoBuilder = Mensagem.newBuilder()
                                        .setTipo(ProtocoloRequisicao.TIPO_REQUISICAO)
                                        .setCodOperacao(ProtocoloRequisicao.OP_AUTENTICAR_USUARIO)
                                        .setIdMensagem(mensagemConvertida.getIdMensagem())
                                        .setNomeUsuarioOrigem(mensagemConvertida.getNomeUsuarioOrigem());

                                requisicaoBuilder.addAllParametros(parametrosLista);

                                requisicao = requisicaoBuilder.build();
                                out.write(requisicao.toByteArray());
                                out.flush();
                                byte[] resposta = new byte[4096];
                                int bytesRead = in.read(resposta, 0, resposta.length);
                                byte[] respostaData = Arrays.copyOf(resposta, bytesRead);
                                Mensagem respostaConvertida = Mensagem.parseFrom(respostaData);
                                if (respostaConvertida.getCodOperacao() == ProtocoloResposta.OP_AUTENTICACAO_OK) {
                                    if (mensagemConvertida.getCodOperacao() == ProtocoloRequisicao.OP_MENSAGEM_GRUPO
                                            || mensagemConvertida.getCodOperacao() == ProtocoloRequisicao.OP_SAIR_GRUPO
                                            || mensagemConvertida
                                            .getCodOperacao() == ProtocoloRequisicao.OP_LISTAR_USUARIOS_ONLINE) {
                                        Mensagem requisicaoAutorizacao = null;
                                        if (mensagemConvertida
                                                .getCodOperacao() == ProtocoloRequisicao.OP_MENSAGEM_GRUPO) {
                                            requisicaoAutorizacao = Mensagem.newBuilder()
                                                    .setTipo(ProtocoloRequisicao.TIPO_REQUISICAO)
                                                    .setCodOperacao(ProtocoloRequisicao.OP_MENSAGEM_GRUPO_AUTORIZAR)
                                                    .setIdMensagem(mensagemConvertida.getIdMensagem())
                                                    .setNomeUsuarioOrigem(mensagemConvertida.getNomeUsuarioOrigem())
                                                    .addAllParametros(parametrosLista).build();
                                        } else if (mensagemConvertida
                                                .getCodOperacao() == ProtocoloRequisicao.OP_SAIR_GRUPO) {
                                            requisicaoAutorizacao = Mensagem.newBuilder()
                                                    .setTipo(ProtocoloRequisicao.TIPO_REQUISICAO)
                                                    .setCodOperacao(ProtocoloRequisicao.OP_SAIR_GRUPO_AUTORIZAR)
                                                    .setIdMensagem(mensagemConvertida.getIdMensagem())
                                                    .setNomeUsuarioOrigem(mensagemConvertida.getNomeUsuarioOrigem())
                                                    .addAllParametros(parametrosLista).build();
                                        } else {
                                            requisicaoAutorizacao = Mensagem.newBuilder()
                                                    .setTipo(ProtocoloRequisicao.TIPO_REQUISICAO)
                                                    .setCodOperacao(
                                                            ProtocoloRequisicao.OP_LISTAR_USUARIOS_ONLINE_AUTORIZAR)
                                                    .setIdMensagem(mensagemConvertida.getIdMensagem())
                                                    .setNomeUsuarioOrigem(mensagemConvertida.getNomeUsuarioOrigem())
                                                    .addAllParametros(parametrosLista).build();
                                        }
                                        out.write(requisicaoAutorizacao.toByteArray());
                                        out.flush();

                                        byte[] respostaAutorizacao = new byte[4096];
                                        int bytesReadAutorizacao = in.read(respostaAutorizacao, 0, respostaAutorizacao.length);
                                        byte[] respostaAutorizacaoData = Arrays.copyOf(respostaAutorizacao, bytesReadAutorizacao);
                                        Mensagem respostaAutorizacaoConvertida = Mensagem
                                                .parseFrom(respostaAutorizacaoData);

                                        if (respostaAutorizacaoConvertida
                                                .getCodOperacao() == ProtocoloResposta.OP_AUTORIZACAO_OK) {
                                            out2.write(mensagemConvertida.toByteArray());
                                            out2.flush();
                                        } else {
                                            Mensagem retornoCliente = Mensagem.newBuilder()
                                                    .setTipo(ProtocoloResposta.TIPO_RESPOSTA)
                                                    .setCodOperacao(ProtocoloResposta.OP_AUTORIZACAO_NOK)
                                                    .setIdMensagem(mensagemConvertida.getIdMensagem())
                                                    .setNomeUsuarioOrigem(mensagemConvertida.getNomeUsuarioOrigem())
                                                    .addParametros("Erro de Autorização").build();

                                            // Send response to client through Redis
                                            jedis2.publish(canalResposta.getBytes(), retornoCliente.toByteArray());
                                        }
                                    } else {
                                        out2.write(mensagemConvertida.toByteArray());
                                        out2.flush();
                                    }
                                } else {
                                    Mensagem retornoCliente = Mensagem.newBuilder()
                                            .setTipo(ProtocoloResposta.TIPO_RESPOSTA)
                                            .setCodOperacao(ProtocoloResposta.OP_AUTENTICACAO_NOK)
                                            .setIdMensagem(mensagemConvertida.getIdMensagem())
                                            .setNomeUsuarioOrigem(mensagemConvertida.getNomeUsuarioOrigem())
                                            .addParametros("Erro de Autenticação").build();

                                    // Send response to client through Redis
                                    jedis2.publish(canalResposta.getBytes(), retornoCliente.toByteArray());
                                }
                            }

                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            };

            Thread leituraThread = new Thread(() -> {
                try {
                    // Start subscribing to the channel
                    jedis.subscribe(jedisPubSub, canalRequisicao.getBytes());
                } finally {
                    // Close resources
                    jedisPubSub.unsubscribe();
                }
            });

            leituraThread.start();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
