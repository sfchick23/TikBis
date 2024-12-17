package ru.sfchick.TikBis.Services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.*;

@Service
public class ParseService {

    private final String URL_QUERY = "https://search.sunbiz.org/Inquiry/CorporationSearch/ByName";
    private final String FORM_URL = "https://search.sunbiz.org/Inquiry/CorporationSearch/ByDocumentNumber";

    public List<String> parseAllCodes() {

        List<String> links = new ArrayList<>();

        List<String> wordsList = Arrays.asList(
                "apple", "sport", "book", "Tik", "dance", "inc"
        );

        // Случайный объект
        Random rand = new Random();

        // Выбор случайного слова из списка
        String randomWord = wordsList.get(rand.nextInt(wordsList.size())).toUpperCase();
        System.out.println(randomWord);

        try {
            // Получаем документ с реальным запросом
            Document doc = Jsoup.connect(URL_QUERY)
                    .data("SearchTerm", randomWord)  // Заполняем поле SearchTerm
                    .data("InquiryType", "FeiNumber")  // Другие скрытые поля формы
                    .data("SearchNameOrder", "")
                    .post();  // Отправляем запрос методом POST


            if (doc == null) {
                System.out.println("Ошибка при получении страницы.");
                return links;
            }

            // Извлекаем строки таблицы с результатами
            Elements rows = doc.select("table tbody tr");


            // Обрабатываем строки таблицы
            for (Element row : rows) {
                // Извлекаем статус
                String status = row.select("td.small-width").text().trim();

                // Логирование статуса для отладки
                System.out.println("Status: " + status);

                // Проверяем, что статус "Active"
                if (status.equals("Active")) {
                    // Извлекаем текст ссылки
//                    String linkText = row.select("td.large-width").text().trim();
                    String numberDoc = row.select("td.medium-width").text().trim();
                    links.add(numberDoc);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return links;
    }

    public List<Map<String, String>> submitFormWithSearchTerm(String searchTerm) throws IOException {
        // Формируем запрос, передавая значение в поле SearchTerm
        Document doc = Jsoup.connect(FORM_URL)
                .data("SearchTerm", searchTerm)  // Заполняем поле SearchTerm
                .data("InquiryType", "FeiNumber")  // Другие скрытые поля формы
                .data("SearchNameOrder", "")
                .post();  // Отправляем запрос методом POST

        // Извлекаем все элементы с классом searchResultDetail
        Elements rows = doc.select(".searchResultDetail");

        // Список для хранения данных
        List<Map<String, String>> results = new ArrayList<>();

        for (Element row : rows) {
            Map<String, String> map = new HashMap<>();

            // Извлекаем имя компании
            String name = row.select(".corporationName p").text().trim();
            if (name.isEmpty()) {
                name = "N/A";  // В случае, если имени нет
            }

            // Извлекаем адрес
            String address = row.select(".detailSection:contains(Principal Address) div").text().trim();
            if (address.isEmpty()) {
                address = "N/A";  // В случае, если адреса нет
            }

            // Извлекаем почтовый индекс из адреса (если есть)
            String zipcode = extractZipCode(address);

            Element documentIdLabel = row.select("label[for=Detail_DocumentId]").first();
            String licenses = documentIdLabel.siblingElements().text().trim();

            // Добавляем данные в карту
            map.put("name", name);
            map.put("country", "USA");
            map.put("address", address);
            map.put("stat", "Florida");
            map.put("zipcode", zipcode);
            map.put("licenses", licenses.split(" ")[0]);

            System.out.println(map);
            results.add(map);
        }
        return results;
    }


    private String extractZipCode(String address) {
        // Удаляем строку с датой изменения
        String cleanedAddress = address.replaceAll("Changed: \\d{2}/\\d{2}/\\d{4}", "").trim();

        // Ищем последние 5 цифр (ZIP код) в строке
        String zipCode = cleanedAddress.replaceAll(".*(\\d{5})$", "$1");
        return zipCode;  // Возвращаем ZIP код
    }

    public List<Map<String, String>> processAndSubmit() {
        try {
            // Получаем список значений из parseAllCodes()
            List<String> links = parseAllCodes();

            // Если ссылки найдены, отправляем первую
            if (!links.isEmpty()) {
                String firstLink = links.get(0);  // Берем первую ссылку из списка
               return submitFormWithSearchTerm(firstLink);  // Отправляем её в форму
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
