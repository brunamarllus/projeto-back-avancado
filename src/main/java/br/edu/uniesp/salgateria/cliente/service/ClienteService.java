package br.edu.uniesp.salgateria.cliente.service;

import br.edu.uniesp.salgateria.cliente.dto.ClienteRequest;
import br.edu.uniesp.salgateria.cliente.dto.ClienteResponse;
import br.edu.uniesp.salgateria.cliente.entity.ClienteEntity;
import br.edu.uniesp.salgateria.cliente.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClienteService {
    private final ClienteRepository clienteRepository;

    public ClienteResponse cadastrarCliente(ClienteRequest clienteRequest) {
        ClienteEntity clienteEntity = new ClienteEntity(
                clienteRequest.nome(),
                clienteRequest.cpf(),
                clienteRequest.telefone(),
                clienteRequest.email(),
                clienteRequest.endereco()
        );
        clienteRepository.save(clienteEntity);

        return new ClienteResponse(clienteEntity.getNome(), clienteEntity.getEmail());
    }
}
