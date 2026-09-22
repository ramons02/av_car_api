package br.edu.senai.fatesg.avcar.core.controllers;

import br.edu.senai.fatesg.avcar.core.dtos.BaseDTO;
import br.edu.senai.fatesg.avcar.core.services.GenericService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

// PADRÃO TEMPLATE METHOD: Define o esqueleto dos endpoints REST (listar, buscarPorId,
// toggleStatus) como métodos concretos, delegando apenas a obtenção do service
// (etapa variável) para as subclasses via getService(). Isso garante que toda
// controller siga o mesmo contrato HTTP sem duplicar código de roteamento.
public abstract class GenericController<D extends BaseDTO> {

   
    
    protected abstract GenericService<?, D, ?> getService();

    
    public ResponseEntity<List<D>> listar(
            @RequestParam(value = "inativos", defaultValue = "false") boolean inativos) {
        return ResponseEntity.ok(getService().listarTodos(inativos));
    }

    
    public ResponseEntity<D> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(getService().buscarPorId(id));
    }

    
    public ResponseEntity<Void> toggleStatus(@PathVariable Long id) {
        getService().toggleStatus(id);
        return ResponseEntity.ok().build();
    }
}
