package br.edu.senai.fatesg.avcar.business.ordemservico;

import br.edu.senai.fatesg.avcar.business.pecas.ItemPecaDTO;
import br.edu.senai.fatesg.avcar.business.servicos.ItemServicoDTO;
import br.edu.senai.fatesg.avcar.business.servicos.ServicoExternoDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordens-servico")
public class OrdemServicoController {

    private final OrdemServicoService service;

    public OrdemServicoController(OrdemServicoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<OrdemServicoDTO>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdemServicoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrdemServicoDTO>> buscarPorStatus(@PathVariable String status) {
        return ResponseEntity.ok(service.buscarPorStatus(status));
    }

    @GetMapping("/dashboard/resumo")
    public ResponseEntity<DashboardDTO> obterResumoDashboard() {
        return ResponseEntity.ok(service.obterResumoDashboard());
    }

    @PostMapping("/{id}/avancar/orcamento")
    public ResponseEntity<OrdemServicoDTO> avancarOrcamento(@PathVariable Long id) {
        return ResponseEntity.ok(service.avancarOrcamento(id));
    }

    @PostMapping("/{id}/avancar/execucao")
    public ResponseEntity<OrdemServicoDTO> avancarExecucao(@PathVariable Long id) {
        return ResponseEntity.ok(service.avancarExecucao(id));
    }

    @PostMapping("/{id}/avancar/pagamento")
    public ResponseEntity<OrdemServicoDTO> avancarPagamento(@PathVariable Long id) {
        return ResponseEntity.ok(service.avancarPagamento(id));
    }

    @PostMapping("/{id}/avancar/finalizar")
    public ResponseEntity<OrdemServicoDTO> finalizar(@PathVariable Long id) {
        return ResponseEntity.ok(service.finalizar(id));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<OrdemServicoDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(service.cancelar(id));
    }

    @PostMapping("/{id}/pausar")
    public ResponseEntity<OrdemServicoDTO> pausar(@PathVariable Long id) {
        return ResponseEntity.ok(service.pausar(id));
    }

    @PostMapping("/{id}/retornar")
    public ResponseEntity<OrdemServicoDTO> retornar(@PathVariable Long id) {
        return ResponseEntity.ok(service.retornar(id));
    }

    @PostMapping("/{id}/garantia")
    public ResponseEntity<OrdemServicoDTO> aplicarGarantia(
            @PathVariable Long id, @RequestParam int dias) {
        return ResponseEntity.ok(service.aplicarGarantiaEstendida(id, dias));
    }

    @GetMapping("/{id}/garantia")
    public ResponseEntity<List<GarantiaDTO>> calcularGarantia(@PathVariable Long id) {
        return ResponseEntity.ok(service.calcularGarantia(id));
    }

    @PostMapping("/{id}/desconto")
    public ResponseEntity<OrdemServicoDTO> aplicarDesconto(
            @PathVariable Long id, @RequestParam double valor) {
        return ResponseEntity.ok(service.aplicarDesconto(id, valor));
    }

    @PostMapping
    public ResponseEntity<OrdemServicoDTO> criar(@RequestBody CriarOSRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(service.criar(req.veiculoId(), req.responsavelId(),
                req.entradaVeiculo(), req.defeitoRelatado(), req.formaPagamento()));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OrdemServicoDTO> atualizar(@PathVariable Long id, @RequestBody AtualizarOSRequest req) {
        return ResponseEntity.ok(service.atualizarCampos(id, req.defeitoRelatado(),
            req.formaPagamento()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/itens-servico")
    public ResponseEntity<List<ItemServicoDTO>> listarItensServico(@PathVariable Long id) {
        return ResponseEntity.ok(service.listarItensServico(id));
    }

    @PostMapping("/{id}/itens-servico")
    public ResponseEntity<ItemServicoDTO> adicionarItemServico(@PathVariable Long id, @RequestBody ItemServicoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(service.adicionarItemServico(id, req.servicoId(), req.quantidade(),
                req.valorUnitario(), req.horaInicio(), req.horaFim(), req.status(), req.colaboradorId()));
    }

    @DeleteMapping("/{id}/itens-servico/{itemId}")
    public ResponseEntity<Void> removerItemServico(@PathVariable Long id, @PathVariable Long itemId) {
        service.removerItemServico(itemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/itens-peca")
    public ResponseEntity<List<ItemPecaDTO>> listarItensPeca(@PathVariable Long id) {
        return ResponseEntity.ok(service.listarItensPeca(id));
    }

    @PostMapping("/{id}/itens-peca")
    public ResponseEntity<ItemPecaDTO> adicionarItemPeca(@PathVariable Long id, @RequestBody ItemPecaRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(service.adicionarItemPeca(id, req.pecaId(), req.quantidade(), req.valorUnitario()));
    }

    @DeleteMapping("/{id}/itens-peca/{itemId}")
    public ResponseEntity<Void> removerItemPeca(@PathVariable Long id, @PathVariable Long itemId) {
        service.removerItemPeca(itemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/servicos-externos")
    public ResponseEntity<List<ServicoExternoDTO>> listarServicosExternos(@PathVariable Long id) {
        return ResponseEntity.ok(service.listarServicosExternos(id));
    }

    @PostMapping("/{id}/servicos-externos")
    public ResponseEntity<ServicoExternoDTO> adicionarServicoExterno(@PathVariable Long id, @RequestBody ServicoExternoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(service.adicionarServicoExterno(id, req.fornecedorId(), req.descricao(), req.valor(), req.garantiaDias()));
    }

    @DeleteMapping("/{id}/servicos-externos/{itemId}")
    public ResponseEntity<Void> removerServicoExterno(@PathVariable Long id, @PathVariable Long itemId) {
        service.removerServicoExterno(itemId);
        return ResponseEntity.noContent().build();
    }

    public record CriarOSRequest(Long veiculoId, Long responsavelId,
                                  String entradaVeiculo, String defeitoRelatado,
                                  String formaPagamento) {}
    public record AtualizarOSRequest(String defeitoRelatado, String formaPagamento) {}
    public record ItemServicoRequest(Long servicoId, int quantidade, double valorUnitario,
                                     String horaInicio, String horaFim, String status, Long colaboradorId) {}
    public record ItemPecaRequest(Long pecaId, int quantidade, double valorUnitario) {}
    public record ServicoExternoRequest(Long fornecedorId, String descricao, double valor, int garantiaDias) {}
}
