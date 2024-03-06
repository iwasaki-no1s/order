package com.example.demo.web.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.common.FlashData;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderDetail;
import com.example.demo.service.BaseService;
import com.example.demo.service.OrderDetailService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/order_details")
public class OrderDetailsController {
	@Autowired
	OrderDetailService orderDetailService;
	@Autowired
	BaseService<Order> orderService;

	/*
	 * 新規作成画面表示
	 */
	@GetMapping(value = "/create/{order_id}")
	public String form(@PathVariable Integer order_id, OrderDetail orderDetail, Model model, RedirectAttributes ra) {
		model.addAttribute("orderDetail", orderDetail);
		try {
			// 存在確認
			Order order = orderService.findById(order_id);
			orderDetail.setOrder(order);
		} catch (Exception e) {
			FlashData flash = new FlashData().danger("該当データがありません");
			ra.addFlashAttribute("flash", flash);
			return "redirect:/admin/orders/list";
		}
		return "admin/order_details/create";
	}

	/*
	 * 新規登録
	 */
	@PostMapping(value = "/create")
	public String register(@Valid OrderDetail orderDetail, BindingResult result, Model model, RedirectAttributes ra) {
		FlashData flash;

		Order order = orderDetail.getOrder();
	    Integer orderId = order.getId();
		try {
			if (result.hasErrors()) {
				return "admin/order_details/create/"+orderId;
			}
			// 新規登録
			orderDetailService.save(orderDetail);
			flash = new FlashData().success("新規作成しました");
		} catch (Exception e) {
			flash = new FlashData().danger("処理中にエラーが発生しました");
		}
		ra.addFlashAttribute("flash", flash);
		return "redirect:/admin/orders/view/"+orderId;
	}

	/*
	 * 編集画面表示
	 */
	@GetMapping(value = "/edit/{id}")
	public String edit(@PathVariable Integer id, Model model, RedirectAttributes ra) {
		try {
			// 存在確認
			OrderDetail orderDetail = orderDetailService.findById(id);
			model.addAttribute("orderDetail", orderDetail);
		} catch (Exception e) {
			FlashData flash = new FlashData().danger("該当データがありません");
			ra.addFlashAttribute("flash", flash);
			return "redirect:/admin/orders";
		}
		return "admin/order_details/edit";
	}

	/*
	 * 更新
	 */
	@PostMapping(value = "/edit/{id}")
	public String update(@PathVariable Integer id, @Valid OrderDetail orderDetail, BindingResult result, Model model, RedirectAttributes ra) {
		FlashData flash;

		try {
			if (result.hasErrors()) {
				return "admin/orders";
			}
			orderDetailService.findById(id);
			orderDetailService.save(orderDetail);
			flash = new FlashData().success("新規作成しました");
		} catch (Exception e) {
			flash = new FlashData().danger("処理中にエラーが発生しました");
		}
		Order order = orderDetail.getOrder();
	    Integer orderId = order.getId();
		ra.addFlashAttribute("flash", flash);
		return "redirect:/admin/orders/view/"+orderId;
	}

	/*
	 * 削除
	 */
	@GetMapping(value = "/delete/{id}")
	public String delete(@PathVariable Integer id, Model model, RedirectAttributes ra) {
		FlashData flash;
		try {
			// 存在確認
			OrderDetail orderDetail = orderDetailService.findById(id);
		} catch (Exception e) {
			flash = new FlashData().danger("該当データがありません");
			ra.addFlashAttribute("flash", flash);
			return "redirect:/admin/orders";
		}

		try {
			orderDetailService.deleteById(id);
			flash = new FlashData().success("削除しました");
		} catch (Exception e) {
			flash = new FlashData().danger("処理中にエラーが発生しました");
		}

		try {
			OrderDetail orderDetail = orderDetailService.findById(id);
			Order order = orderDetail.getOrder();
			Integer orderId = order.getId();
			return "redirect:admin/orders/view/"+orderId;
		} catch (Exception e) {
			return "redirect:/admin/orders";
		}
	}
}