package com.prs.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import com.prs.business.Product;
import com.prs.db.ProductRepository;


@CrossOrigin
@RestController
@RequestMapping("/products")
public class ProductController {

		@Autowired
		private ProductRepository productRepo;

		@GetMapping("/")
		public JsonResponse listProducts() {
			JsonResponse jr = null;
			try {
				jr = JsonResponse.getInstance(productRepo.findAll());

			} catch (Exception e) {
				jr = JsonResponse.getInstance("Error getting all Products");
				e.printStackTrace();
			}
			return jr;
		}

		// return 1 product for the given id
		@GetMapping("/{id}")
		public JsonResponse getProduct(@PathVariable int id) {
			JsonResponse jr = null;
			try {
				jr = JsonResponse.getInstance(productRepo.findById(id));
			} catch (Exception e) {
				jr = JsonResponse.getInstance(e);
				e.printStackTrace();
			}
			return jr;
		}

		// add a new product
		@PostMapping("/")
		public JsonResponse addProduct(@RequestBody Product p) {
			JsonResponse jr = null;

			try {
				jr = JsonResponse.getInstance(productRepo.save(p));
			} catch (DataIntegrityViolationException dive) {
				jr = JsonResponse.getInstance(dive.getRootCause().getMessage());
				dive.printStackTrace();
			} catch (Exception e) {
				jr = JsonResponse.getInstance(e);
				e.printStackTrace();
			}
			return jr;
		}

		// update a product
		@PutMapping("/")
		public JsonResponse updateProduct(@RequestBody Product p) {
			JsonResponse jr = null;
			try {
				if (productRepo.existsById(p.getId())) {
					jr = JsonResponse.getInstance(productRepo.save(p));
				} else {
					// record doesn't exist
					jr = JsonResponse.getInstance("Error updating Product. id: " + p.getId() + " doesn't exist.");
				}
			} catch (DataIntegrityViolationException dive) {
				jr = JsonResponse.getInstance(dive.getRootCause().getMessage());
				dive.printStackTrace();
			} catch (Exception e) {
				jr = JsonResponse.getInstance(e);
				e.printStackTrace();
			}
			return jr;
		}

		@DeleteMapping("/{id}")
		public JsonResponse deleteProduct(@PathVariable int id) {
			JsonResponse jr = null;
			try {
				if (productRepo.existsById(id)) {
					productRepo.deleteById(id);
					jr = JsonResponse.getInstance("Delete successful");
				} else {
					// record doesn't exist
					jr = JsonResponse.getInstance("Error deleting Product. id: " + id + " doesn't exist.");
				}
			} catch (DataIntegrityViolationException dive) {
				jr = JsonResponse.getInstance(dive.getRootCause().getMessage());
				dive.printStackTrace();
			} catch (Exception e) {
				jr = JsonResponse.getInstance(e);
				e.printStackTrace();
			}
			return jr;
		}

	}



