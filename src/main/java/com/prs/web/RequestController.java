package com.prs.web;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import com.prs.business.Request;
import com.prs.business.User;
import com.prs.db.RequestRepository;


@CrossOrigin
@RestController
@RequestMapping("/requests")
public class RequestController {

		@Autowired
		private RequestRepository requestRepo;

		@GetMapping("/")
		public JsonResponse listRequests() {
			JsonResponse jr = null;
			try {
				jr = JsonResponse.getInstance(requestRepo.findAll());

			} catch (Exception e) {
				jr = JsonResponse.getInstance("Error getting all Requests");
				e.printStackTrace();
			}
			return jr;
		}

		// return 1 request for the given id
		@GetMapping("/{id}")
		public JsonResponse getRequest(@PathVariable int id) {
			JsonResponse jr = null;
			try {
				jr = JsonResponse.getInstance(requestRepo.findById(id));
			} catch (Exception e) {
				jr = JsonResponse.getInstance(e);
				e.printStackTrace();
			}
			return jr;
		}
		
		@GetMapping("/list-review/{id}")
		public JsonResponse listLineItemByRequest(@PathVariable int id) {
			JsonResponse jr = null;
			try {
				jr = JsonResponse.getInstance(requestRepo.findByUserIdNotAndStatusEquals(id, "Review"));
				
				
				
			} catch (Exception e) {
				jr = JsonResponse.getInstance(e);
				e.printStackTrace();
			}
			return jr;
		}

		// add a new request
		@PostMapping("/")
		public JsonResponse addRequest(@RequestBody Request r) {
			JsonResponse jr = null;

			try {
				jr = JsonResponse.getInstance(requestRepo.save(r));
			
			} catch (DataIntegrityViolationException dive) {
				jr = JsonResponse.getInstance(dive.getRootCause().getMessage());
				dive.printStackTrace();
			} catch (Exception e) {
				jr = JsonResponse.getInstance(e);
				e.printStackTrace();
			}
			return jr;
		}

		// update a request
		@PutMapping("/")
		public JsonResponse updateRequest(@RequestBody Request r) {
			JsonResponse jr = null;
			try {
				if (requestRepo.existsById(r.getId())) {
					jr = JsonResponse.getInstance(requestRepo.save(r));
				} else {
					// record doesn't exist
					jr = JsonResponse.getInstance("Error updating Request. id: " + r.getId() + " doesn't exist.");
				}
			
			} catch (Exception e) {
				jr = JsonResponse.getInstance(e);
				e.printStackTrace();
			}
			return jr;
		}
		
		@PutMapping("/submit-review")
		public JsonResponse submitForReview(@RequestBody Request r) {
			JsonResponse jr = null;
			try {
				if (requestRepo.existsById(r.getId())) {
					if (r.getTotal() <= 50) {
						r.setStatus("Approved");
					} else {
						r.setStatus("Review");
					}
					r.setSubmittedDate(LocalDateTime.now());
					jr = JsonResponse.getInstance(requestRepo.save(r));
				} else {
					// record doesn't exist
					jr = JsonResponse.getInstance("Error updating Request. id: " + r.getId() + " doesn't exist.");
				}
			
			} catch (Exception e) {
				jr = JsonResponse.getInstance(e);
				e.printStackTrace();
			}
			return jr;
		}
		
		@PutMapping("/requests/approve")
		public JsonResponse approve(@RequestBody Request r) {
			JsonResponse jr = null;
			try {
				if (requestRepo.existsById(r.getId())) {
					r.setStatus("Approved");
					jr = JsonResponse.getInstance(requestRepo.save(r));
				} else {
					// record doesn't exist
					jr = JsonResponse.getInstance("Error updating Request. id: " + r.getId() + " doesn't exist.");
				}
			
			} catch (Exception e) {
				jr = JsonResponse.getInstance(e);
				e.printStackTrace();
			}
			return jr;
		}
		
		@PutMapping("/requests/reject")
		public JsonResponse reject(@RequestBody Request r) {
			JsonResponse jr = null;
			try {
				if (requestRepo.existsById(r.getId())) {
					r.setStatus("Rejected");
					jr = JsonResponse.getInstance(requestRepo.save(r));
				} else {
					// record doesn't exist
					jr = JsonResponse.getInstance("Error updating Request. id: " + r.getId() + " doesn't exist.");
				}
			
			} catch (Exception e) {
				jr = JsonResponse.getInstance(e);
				e.printStackTrace();
			}
			return jr;
		}

		@DeleteMapping("/{id}")
		public JsonResponse deleteRequest(@PathVariable int id) {
			JsonResponse jr = null;
			try {
				if (requestRepo.existsById(id)) {
					requestRepo.deleteById(id);
					jr = JsonResponse.getInstance("Delete successful");
				} else {
					// record doesn't exist
					jr = JsonResponse.getInstance("Error deleting Request. id: " + id + " doesn't exist.");
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



