package com.example.loja_brinquedos.service;

import com.example.loja_brinquedos.model.Brinquedo;
import com.example.loja_brinquedos.model.Categoria;
import com.example.loja_brinquedos.model.Imagem;
import com.example.loja_brinquedos.repository.BrinquedoRepository;
import com.example.loja_brinquedos.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.stream.Collectors;
import java.util.*;


@Service
public class BrinquedoService {

    private final BrinquedoRepository brinquedoRepository;
    private final CategoriaRepository categoriaRepository;
    private final CloudinaryService cloudinaryService;

    public BrinquedoService (BrinquedoRepository brinquedoRepository, CategoriaRepository categoriaRepository, CloudinaryService cloudinaryService) {
        this.brinquedoRepository = brinquedoRepository;
        this.categoriaRepository = categoriaRepository;
        this.cloudinaryService = cloudinaryService;
    }

    public List<Brinquedo> findAll() {
        return brinquedoRepository.findAll();
    }

    public List<Brinquedo> findTop8MaisAcessados() {
        return brinquedoRepository.findTop8ByOrderByViewsDesc();
    }

    public List<Brinquedo> findByCategoriaId(Long idCategoria) {
        return brinquedoRepository.findByCategorias_Id(idCategoria);
    }

    public Optional<Brinquedo> findById(Long id) {
        return brinquedoRepository.findById(id);
    }

    public List<Brinquedo> filtrarPorCategoria(Long categoriaId, String nome, List<String> marcas,
                                               BigDecimal minValor, BigDecimal maxValor) {
        // se a lista de marcas estiver vazia, passamos null
        if (marcas != null && marcas.isEmpty()) {
            marcas = null;
        }

        return brinquedoRepository.filtrarPorCategoria(categoriaId, nome, marcas, minValor, maxValor);
    }

    public Map<String, Object> getBrinquedoComRelacionados(Long id) {
        // Busca o brinquedo principal
        Brinquedo brinquedo = brinquedoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brinquedo não encontrado"));

        // Incrementa visualizações
        brinquedo.setViews(brinquedo.getViews() + 1);
        brinquedoRepository.save(brinquedo);

        // Busca os brinquedos relacionados (mesma(s) categoria(s), exceto o atual)
        Set<Categoria> categorias = brinquedo.getCategorias();
        List<Brinquedo> relacionados = brinquedoRepository.findRelacionadosByCategorias(categorias, id);

        // Simplifica os relacionados — só imagem principal, nome e valor
        List<Map<String, Object>> relacionadosSimplificados = relacionados.stream().map(b -> {
            Map<String, Object> mapa = new HashMap<>();
            mapa.put("id", b.getId());
            mapa.put("nome", b.getNome());
            mapa.put("valor", b.getValor());

            // Pega a primeira imagem, se existir
            String imagemPrincipal = (b.getImagens() != null && !b.getImagens().isEmpty())
                    ? b.getImagens().get(0).getCaminho()
                    : null;
            mapa.put("imagem", imagemPrincipal);

            return mapa;
        }).collect(Collectors.toList());

        // Monta o retorno
        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("brinquedo", brinquedo);
        resposta.put("relacionados", relacionadosSimplificados);

        return resposta;
    }

    public List<String> findAllMarcas() {
        return brinquedoRepository.findAll()
                .stream()
                .map(Brinquedo::getMarca)
                .filter(Objects::nonNull)
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
    }

    public Brinquedo criarBrinquedo(String codigo, String nome, BigDecimal valor, String marca,
                                    String descricao, String detalhes, List<Long> categoriaIds,
                                    List<MultipartFile> arquivos) throws Exception {

        // Cria o brinquedo
        Brinquedo brinquedo = new Brinquedo();
        brinquedo.setCodigo(codigo);
        brinquedo.setNome(nome);
        brinquedo.setValor(valor);
        brinquedo.setMarca(marca);
        brinquedo.setDescricao(descricao);
        brinquedo.setDetalhes(detalhes);

        // Associa categorias
        Set<Categoria> categorias = new HashSet<>();
        for (Long id : categoriaIds) {
            categoriaRepository.findById(id).ifPresent(categorias::add);
        }
        brinquedo.setCategorias(categorias);

        // Faz upload das imagens
        List<Imagem> imagens = new ArrayList<>();
        for (MultipartFile arquivo : arquivos) {
            if (!arquivo.isEmpty()) {
                Map<String, Object> uploadResult = cloudinaryService.uploadFile(arquivo);
                String imageUrl = (String) uploadResult.get("secure_url");
                String publicId = (String) uploadResult.get("public_id");

                Imagem imagem = new Imagem();
                imagem.setCaminho(imageUrl);
                imagem.setPublicId(publicId);
                imagem.setBrinquedo(brinquedo);

                imagens.add(imagem);
            }
        }

        brinquedo.setImagens(imagens);

        return brinquedoRepository.save(brinquedo);
    }

    public Brinquedo save(Brinquedo brinquedo) {
        return brinquedoRepository.save(brinquedo);
    }

    public void delete(Long id) {
        brinquedoRepository.deleteById(id);
    }

}
