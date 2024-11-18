package com.example.funko.category.storage.json;

import com.example.funko.category.dto.input.InputCategory;
import com.example.funko.category.model.Category;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.List;

public interface CategoryJsonStorage {
    Flux<InputCategory> getCategoriesFromFile(File file);

    void saveCategoriesToFile(File file, List<Category> categories);
}
