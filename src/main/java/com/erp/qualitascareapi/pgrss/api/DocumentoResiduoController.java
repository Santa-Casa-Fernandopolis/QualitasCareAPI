package com.erp.qualitascareapi.pgrss.api;

import com.erp.qualitascareapi.pgrss.api.dto.DocumentoResiduoDto;
import com.erp.qualitascareapi.pgrss.application.DocumentoResiduoService;
import com.erp.qualitascareapi.pgrss.enums.TipoDocumentoResiduo;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/pgrss")
public class DocumentoResiduoController {

    private final DocumentoResiduoService service;

    public DocumentoResiduoController(DocumentoResiduoService service) {
        this.service = service;
    }

    @PostMapping("/coletas-externas/{id}/documentos/upload")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.PGRSS_COLETA_EXTERNA, action = Action.UPDATE)
    public DocumentoResiduoDto upload(@PathVariable Long id,
                                       @RequestParam MultipartFile file,
                                       @RequestParam TipoDocumentoResiduo tipoDocumento,
                                       @RequestParam(required = false) String uploadadoPorNome) throws IOException {
        byte[] data = file.getBytes();
        String nomeArquivo = file.getOriginalFilename() != null ? file.getOriginalFilename() : file.getName();
        String mimeType = file.getContentType();
        return service.upload(id, tipoDocumento, nomeArquivo, mimeType, data, uploadadoPorNome);
    }

    @GetMapping("/coletas-externas/{id}/documentos")
    @RequiresPermission(resource = ResourceType.PGRSS_COLETA_EXTERNA, action = Action.READ)
    public List<DocumentoResiduoDto> list(@PathVariable Long id) {
        return service.listByColetaExterna(id);
    }
}
