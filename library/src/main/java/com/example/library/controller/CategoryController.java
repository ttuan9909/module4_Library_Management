    package com.example.library.controller;

    import com.example.library.dto.CategoryDTO;
    import com.example.library.entity.Category;
    import com.example.library.repository.CategoryRepository;
    import jakarta.validation.Valid;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.data.domain.Pageable;
    import org.springframework.data.domain.Sort;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.validation.BindingResult;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.servlet.mvc.support.RedirectAttributes;

    @RequestMapping("/category")
    @Controller
    public class CategoryController {
        @Autowired
        private CategoryRepository categoryRepository;
       @GetMapping("/list")
        public String list(@RequestParam(name = "page", required = false, defaultValue = "0") int page,
                           @RequestParam(name = "searchName", required = false, defaultValue = "") String searchName
               ,Model model) {
           Pageable pageable = PageRequest.of(page, 6, Sort.by("categoryId").ascending());
    Page<Category> categoryPage = categoryRepository.findByCategoryNameContaining(searchName,pageable);
    model.addAttribute("categoryPage", categoryPage);
    model.addAttribute("searchName", searchName);
    return "admin/category/list";
       }
       @GetMapping("/add")
        public String showAddForm(Model model) {
           model.addAttribute("categoryDTO", new CategoryDTO());
           return "admin/category/add";
       }
@PostMapping("/add")
        public String add(@Valid @ModelAttribute("categoryDTO") CategoryDTO categoryDTO,
                          BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "admin/category/add";
        }
        if(categoryRepository.existsByCategoryNameContaining(categoryDTO.getCategoryName())){
            model.addAttribute("nameError","Tên thể loại đã tồn tại");
            return "admin/category/add";
        }
    Category category = new Category();
    category.setCategoryName(categoryDTO.getCategoryName());
    category.setDescription(categoryDTO.getDescription());

    categoryRepository.save(category);
    return "redirect:/category/list";
}
@GetMapping("/delete/{id}")
        public String delete(@PathVariable int id, RedirectAttributes redirectAttributes) {
           categoryRepository.deleteById(id);
           redirectAttributes.addFlashAttribute("mess","Xoá cầu thủ thành công");
           return "redirect:/category/list";
}
@GetMapping("/update/{id}")
        public String showUpdateForm(@PathVariable("id") int id, Model model) {
           Category category=categoryRepository.findById(id).get();
           model.addAttribute("category", category);
           return "admin/category/update";
}
@PostMapping("/update")
        public String update(@Valid @ModelAttribute("categoryDTO") CategoryDTO categoryDTO,
                             BindingResult bindingResult, Model model){
    if (bindingResult.hasErrors()) {
        return "admin/category/update";
    }
    Category category = categoryRepository.findById(categoryDTO.getCategoryId()).get();
    category.setCategoryName(categoryDTO.getCategoryName());
    category.setDescription(categoryDTO.getDescription());
    categoryRepository.save(category);
    return "redirect:/category/list";
                             }

    }
