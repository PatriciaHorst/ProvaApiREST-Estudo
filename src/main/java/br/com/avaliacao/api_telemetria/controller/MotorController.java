package br.com.avaliacao.api_telemetria.controller;

import br.com.avaliacao.api_telemetria.dto.MotorRequestDTO;
import br.com.avaliacao.api_telemetria.dto.MotorResponseDTO;
import br.com.avaliacao.api_telemetria.dto.MotorStatusUpdateDTO;
import br.com.avaliacao.api_telemetria.service.MotorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Motor", description = "Recebe, consulta e retorna requisições do Motor")
@RestController
@RequestMapping("/api/v1/motores")
public class MotorController {

    private final MotorService service;

    public MotorController(MotorService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Cria um motor",
            description = "Cadastra um novo motor")
    @ApiResponses (value = {
            @ApiResponse(responseCode = "201",
                headers = @Header(name = "Location", description = "URI do motor criada"),
                description = "Motor criado!"),
            @ApiResponse(responseCode = "400",
                description = "Erro na validação de dados inseridos pelo usuario ou motor ja existente"),
            @ApiResponse(responseCode = "404",
                description = "Motor não foi criado porque setor nao foi encontrado")
    })
    public ResponseEntity<MotorResponseDTO> criar(
            @Valid @RequestBody MotorRequestDTO request) {
        MotorResponseDTO response = service.criar(request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);

    }

    @GetMapping
    @Operation(summary = "Lista todos os motores",
            description = "Retorna todos os motores cadastrados")
    @ApiResponses (value = {
            @ApiResponse(responseCode = "200",
                description = "Todos os motores foram retornados (pode ser vazio)")
    })
    public ResponseEntity<Page<MotorResponseDTO>> listarTodos(
            @PageableDefault(size = 20, sort = "id")Pageable pageable)
    {
        return ResponseEntity.ok(service.listarTodos(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um motor pelo ID",
            description = "Retorna o motor do ID inserido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                description = "O motor foi encontrado com sucesso!"),
            @ApiResponse(responseCode = "404",
                description = "Não foi possivel achar o motor")
    })
    public ResponseEntity<MotorResponseDTO> buscarPorId(
            @Parameter(description = "Id de um motor", example = "1")
            @PathVariable Integer id)
    {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/codigo")
    @Operation(summary = "Lista motores ativos",
        description = "Retorna os motores com o codigo ATIVO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                description = "Motores ativos retornados"),
            @ApiResponse(responseCode = "400",
                description = "Erro na validação dos dados inseridos pelo usuario"),
            @ApiResponse(responseCode = "404",
                description = "Não foi possivel achar os motores")
    })
    public ResponseEntity<MotorResponseDTO> buscarPorCodigoAtivo(
            @RequestParam String codigoAtivo)
    {
        return ResponseEntity.ok(service.buscarPorCodigoAtivo(codigoAtivo));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um motor",
        description = "Atualiza os dados de um motor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                description = "O motor foi atualizado com sucesso!"),
            @ApiResponse(responseCode = "400",
                description = "Erro na validação de dados inseridos pelo usuario"),
            @ApiResponse(responseCode = "404",
                description = "Não foi possivel achar o motor ou setor")
    })
    public ResponseEntity<MotorResponseDTO> atualizar(
            @Parameter(description = "Id do motor", example = "10")
            @PathVariable Integer id,
            @Valid @RequestBody MotorRequestDTO request)
    {
        return ResponseEntity.ok(service.atualizar(id,request));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualiza status",
    description = "Atualiza somente o status do motor")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "200",
                description = "O status foi atualizado"),
            @ApiResponse(responseCode = "400",
                description = "Erro na validação dos dados inseridos pelo usuario"),
            @ApiResponse(responseCode = "404",
                description = "Não foi possivel encontrar o motor")
    })
    public ResponseEntity<MotorResponseDTO> atualizarStatus(
            @Parameter(description = "Id do motor", example = "5")
            @PathVariable Integer id,
            @Valid @RequestBody MotorStatusUpdateDTO dto)
    {
        return ResponseEntity.ok(service.atualizarStatus(id, dto));
    }
}
