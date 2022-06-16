package com.micropos.products.repository;

import com.micropos.products.model.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class JDRepository implements ProductRepository {

    private volatile List<Product> products = null;

    public List<Product> allProducts() {
        if (products == null) {
            synchronized (this) {
                if (products == null) {
                    try {
                        products = parseJD("java");
                        products = products.stream().filter(p -> !p.getId().isBlank()).collect(Collectors.toList());
                    } catch (IOException e) {
                        products = null;
                        e.printStackTrace();
                    }
                }
            }
        }
        return products;
    }

    @Override
    public Mono<Optional<Product>> findProduct(String productId) {
        return Mono.fromCallable(() -> {
            for (Product p : allProducts()) {
                if (p.getId().equals(productId)) {
                    return Optional.of(p);
                }
            }
            return Optional.empty();
        });
    }

    @Override
    public Mono<Integer> productsCount() {
        return Mono.fromCallable(() -> {
            List<Product> products = allProducts();
            return products == null ? 0 : products.size();
        });
    }

    // 这里只要List，不要Flux；使用Mono只是为了让查询变成一个异步操作
    @Override
    public Mono<List<Product>> productsInRange(int fromIndex, int count) {
        return Mono.fromCallable(() -> {
            List<Product> products = allProducts();
            if (products == null || fromIndex < 0 || fromIndex >= products.size()) {
                return new ArrayList<>();
            }
            int toIndex = Math.min(fromIndex + count, products.size());
            return products.subList(fromIndex, toIndex);
        });
    }

    public List<Product> parseJD(String keyword) throws IOException {
        String url = "https://search.jd.com/Search?keyword=" + keyword;
        Document document = Jsoup.parse(new URL(url), 10000);
        Element element = document.getElementById("J_goodsList");
        if (element == null) {
            return null;
        }
        Elements elements = element.getElementsByTag("li");

        List<Product> list = new ArrayList<>();
        for (Element el : elements) {
            String id = el.attr("data-spu");
            String img = "https:".concat(el.getElementsByTag("img").eq(0).attr("data-lazy-img"));
            String price = el.getElementsByAttribute("data-price").text();
            String title = el.getElementsByClass("p-name").eq(0).text();
            if (title.contains("，")) {
                title = title.substring(0, title.indexOf("，"));
            }
            Product product = new Product(id, title, Double.parseDouble(price), img);
            list.add(product);
        }

        return list;
    }
}
