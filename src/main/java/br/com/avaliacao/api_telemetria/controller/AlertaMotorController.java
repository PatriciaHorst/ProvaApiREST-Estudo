package br.com.avaliacao.api_telemetria.controller;

import br.com.avaliacao.api_telemetria.dto.AlertaRequestDTO;
import br.com.avaliacao.api_telemetria.dto.AlertaResolucaoDTO;
import br.com.avaliacao.api_telemetria.dto.AlertaResponseDTO;
import br.com.avaliacao.api_telemetria.service.AlertaMotorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Alerta", description = "Registra, consulta e devolve dados de acordo com as requisiçoes do Alerta ")
@RestController
@RequestMapping("/api/v1/alertas")
public class AlertaMotorController {

    private final AlertaMotorService service;

    public AlertaMotorController(AlertaMotorService service){
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Registra um novo alerta",
            description = "Recebe os dados, valida e cria um alerta de acordo com um motor existente")
    @ApiResponses (value = {
            @ApiResponse(responseCode = "201",
                    headers = @Header(name = "Location", description = "URI do alerta criado com sucesso"),
                    description = "O alerta foi criado com sucesso!"),
            @ApiResponse(responseCode = "400",
                    description = "Erro na validação dos dados inseridos pelo usuario"),
            @ApiResponse(responseCode = "404",
                    description = "Não foi possivel achar o motor")
    })
    public ResponseEntity<AlertaResponseDTO> criar(
        @Valid @RequestBody AlertaRequestDTO request) {

        AlertaResponseDTO response = service.criar(request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Lista todos os alertas",
            description = "Retorna todos os alertas registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Todos os alertas foram retornados")
    })
    public ResponseEntity<Page<AlertaResponseDTO>> listarTodos(
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(service.listarTodos(pageable));
    }

    @GetMapping("/motor/{motorId}")
    @Operation(summary = "Lista os alertas de um motor",
            description = "Retorna todos os alertas registrados de um motor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Alertas do motor foram retornados"),
            @ApiResponse(responseCode = "400",
                    description = "Erro na validação dos dados inseridos pelo usario"),
            @ApiResponse(responseCode = "404",
                    description = "Não foi possivel encontrar o motor")
    })
    public ResponseEntity<Page<AlertaResponseDTO>> buscarPorMotor(
            @Parameter(description = "ID do motor com alerta registrado", example = "1")
            @PathVariable Integer motorId,
            @PageableDefault(size = 20)Pageable pageable)
    {
        return ResponseEntity.ok(service.buscarPorMotor(motorId,pageable));
    }


    @PatchMapping("/{id}")
    @Operation(summary = "Atualiza o status do alerta",
            description = "Atualiza o status do alerta como resolvido ou não resolvido")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "200",
                    description = "A atualização foi feita com sucesso!"),
            @ApiResponse(responseCode = "400",
                    description = "Erro na validação dos dados inseridos para atualização"),
            @ApiResponse(responseCode = "404",
                    description = "Alerta não foi encontrado")
    })
    public ResponseEntity<AlertaResponseDTO> atualizarStatusResolucao(
            @Parameter(description = "ID do alerta", example = "1")
            @PathVariable Integer id,
            @Valid @RequestBody AlertaResolucaoDTO dto)
    {
        return ResponseEntity.ok(service.atualizarStatusResolucao(id, dto));
    }
}
