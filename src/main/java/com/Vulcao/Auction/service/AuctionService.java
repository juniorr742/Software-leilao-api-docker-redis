package com.Vulcao.Auction.service;

import com.Vulcao.Auction.dto.response.PageResponse;
import com.Vulcao.Auction.model.Auction;
import com.Vulcao.Auction.repositorys.IAuctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class AuctionService {

    @Autowired
    IAuctionRepository repository;

    public PageResponse<Auction> buscarTodasAsPaginas(int paginas){
        Pageable pageable = PageRequest.of(paginas, 100);
        Page<Auction> auctionPage = repository.findAll(pageable);

        return PageResponse.<Auction>builder().conteudo(auctionPage.getContent()).totalElementos(auctionPage.getTotalElements()).
                totalPaginas(auctionPage.getTotalPages()).paginaAtual(auctionPage.getNumber()).tamanhoPagina(auctionPage.getSize()).build();
    }
}
