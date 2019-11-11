package com.prs.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import com.prs.business.LineItem;
import com.prs.business.Request;
import com.prs.db.LineItemRepository;
import com.prs.db.RequestRepository;

@CrossOrigin
@RestController
@RequestMapping("/line-items")
public class LineItemController {
	@Autowired
	private LineItemRepository lineitemRepo;
	@Autowired
	private RequestRepository requestRepo;

	@GetMapping("/")
	public JsonResponse listLineItems() {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(lineitemRepo.findAll());

		} catch (Exception e) {
			jr = JsonResponse.getInstance("Error getting all LineItems");
			e.printStackTrace();
		}
		return jr;
	}

	// return 1 lineitem for the given id
	@GetMapping("/{id}")
	public JsonResponse getLineItem(@PathVariable int id) {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(lineitemRepo.findById(id));
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		return jr;
	}
	
	@GetMapping("/lines-for-pr/{id}")
	public JsonResponse listLineItemByRequest(@PathVariable int id) {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(lineitemRepo.findByRequestId(id));
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		return jr;
	}

	// add a new lineitem
	@PostMapping("/")
	public JsonResponse addLineItem(@RequestBody LineItem l) {
		JsonResponse jr = null;

		try {
			jr = JsonResponse.getInstance(lineitemRepo.save(l));
			recalculateTotal(l.getRequest());
			
		} catch (DataIntegrityViolationException dive) {
			jr = JsonResponse.getInstance(dive.getRootCause().getMessage());
			dive.printStackTrace();
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		return jr;
	}

	// update a lineitem
	@PutMapping("/")
	public JsonResponse updateLineItem(@RequestBody LineItem l) {
		JsonResponse jr = null;
		try {
			if (lineitemRepo.existsById(l.getId())) {
				jr = JsonResponse.getInstance(lineitemRepo.save(l));
				recalculateTotal(l.getRequest());
			} else {
				// record doesn't exist
				jr = JsonResponse.getInstance("Error updating LineItem. id: " + l.getId() + " doesn't exist.");
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
	public JsonResponse deleteLineItem(@PathVariable int id) {
		JsonResponse jr = null;
		try {
			if (lineitemRepo.existsById(id)) {
				lineitemRepo.deleteById(id);
				jr = JsonResponse.getInstance("Delete successful");
				Request r = lineitemRepo.findById(id).get().getRequest();
				recalculateTotal(r);
			} else {
				// record doesn't exist
				jr = JsonResponse.getInstance("Error deleting LineItem. id: " + id + " doesn't exist.");
			}
		
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		return jr;
	}
	
	private void recalculateTotal(Request r) {
		List<LineItem> lineitems = lineitemRepo.findByRequestId(r.getId());
		double sum = 0;
		for (LineItem l : lineitems) {
			sum += l.getTotal();
		}
		r.setTotal(sum);
		try {
			requestRepo.save(r);
		} catch (Exception e) {
			throw e;
		}
		
	}


}
