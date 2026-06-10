package com.Vulcao.Auction.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {

    private List<T> conteudo;
    private long totalElementos;
    private int totalPaginas;
    private int paginaAtual;
    private int tamanhoPagina;

}
