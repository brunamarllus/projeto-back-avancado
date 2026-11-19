package br.edu.uniesp.salgateria.pedido.service;

import br.edu.uniesp.salgateria.cliente.entity.ClienteEntity;
import br.edu.uniesp.salgateria.cliente.repository.ClienteRepository;
import br.edu.uniesp.salgateria.produto.entity.ProdutoEntity;
import br.edu.uniesp.salgateria.produto.repository.ProdutoRepository;
import br.edu.uniesp.salgateria.pedido.dto.PedidoRequest;
import br.edu.uniesp.salgateria.pedido.dto.PedidoResponse;
import br.edu.uniesp.salgateria.pedido.entity.PedidoEntity;
import br.edu.uniesp.salgateria.pedido.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    @Transactional
    public PedidoResponse criarPedido(PedidoRequest pedidoRequest) {

        ClienteEntity cliente = clienteRepository.findById(pedidoRequest.clienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));

        ProdutoEntity produto = produtoRepository.findById(pedidoRequest.produtoId())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

        PedidoEntity pedidoEntity = new PedidoEntity(
                cliente,
                pedidoRequest.dataEntregaPrevista(),
                pedidoRequest.observacao(),
                pedidoRequest.responsavel()
        );

        pedidoEntity.adicionarItem(produto, pedidoRequest.quantidade());

        pedidoRepository.save(pedidoEntity);

        return new PedidoResponse(
                pedidoEntity.getId(),
                cliente.getId(),
                produto.getId(),
                pedidoRequest.quantidade(),
                pedidoEntity.valorTotal(),
                pedidoEntity.getStatus().name(),
                pedidoEntity.getCodigoRastreio(),
                pedidoEntity.getDataEntregaPrevista(),
                pedidoEntity.getDataPedido()
        );
    }
}