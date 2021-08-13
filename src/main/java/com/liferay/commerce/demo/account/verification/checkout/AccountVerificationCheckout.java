package com.liferay.commerce.demo.account.verification.checkout;

import com.liferay.commerce.account.model.CommerceAccount;
import com.liferay.commerce.constants.CommerceCheckoutWebKeys;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.util.BaseCommerceCheckoutStep;
import com.liferay.commerce.util.CommerceCheckoutStep;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Jeffrey Handa
 */
@Component(
	immediate = true,
	property = {
			"commerce.checkout.step.name=" + AccountVerificationCheckout.NAME,
			"commerce.checkout.step.order:Integer=55"
	},
	service = CommerceCheckoutStep.class
)
public class AccountVerificationCheckout extends BaseCommerceCheckoutStep {

	public static final String NAME = "account-verification";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getLabel(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
				"content.Language", locale, getClass());

		return LanguageUtil.get(resourceBundle, "account-verification");
	}

	@Override
	public boolean isActive(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {

		/*
			It would be very common to use the CommerceContext at this point to get current context
			(Channel, Account, etc.) and then determine if the Checkout Step should be used.  For example,
			we could add a Custom Field on the Account Object to determine if Account Verification is required,
			or we could simply check for any previous orders and require verification for accounts that have no '
			Completed' orders.  You might also want to require Account Verifiation in the B2B channel, since they
			are getting wholesale pricing, but you don't need to do that in the B2C channel.
		 */

		CommerceContext commerceContext = (CommerceContext) httpServletRequest.getAttribute(CommerceWebKeys.COMMERCE_CONTEXT);
		CommerceAccount commerceAccount = commerceContext.getCommerceAccount();
		long commerceChannelId = commerceContext.getCommerceChannelId();
		CommerceChannel commerceChannel = _commerceChannelLocalService.getCommerceChannel(commerceChannelId);

		/*
			It might also be common to put a check here that checks for the items in the cart to determine if a
			checkout step is required or not.  For example, if the product is in the hazardous materials category, then
			we need to ensure customer is qualified to receive those materials.
		 */

		return true;
	}

	@Override
	public boolean isVisible(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {

		/*
			This flag determines if the label shows up in the 'timeline' across the top of the checkout widget.
			Even if this is set to false, the Render method will still get called and while on this checkout step
			the label displays.  After completing the step, if the isVisible is still set to false, the label
			disappears from the timeline again.
		 */
		return true;
	}

	@Override
	public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {

		/*
			Just like in a Portlet, this is your opportunity to process the data submitted in the form before
			proceeding to the next step.  If we are storing the Verification status of the Account as a custom
			field or using and Account Group, this would be the place to call the Account service or Account Group
			service and make those updates.
		 */

	}

	@Override
	public void render(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {

		/*
			This is a good place to create a 'displayContext' object or helper object that will help your
			checkout step's view.  From the httpServletRequest you can get the CommerceContext to get the Account
			and Channel or get the Order to get the Order Line Items.

			Once you've created your context, or collected any other useful data, you can set them as Attributes
			on the httpServletRequest.
		 */

		CommerceContext commerceContext = (CommerceContext) httpServletRequest.getAttribute(CommerceWebKeys.COMMERCE_CONTEXT);
		CommerceAccount commerceAccount = commerceContext.getCommerceAccount();
		httpServletRequest.setAttribute("commerceAccount", commerceAccount);

		_jspRenderer.renderJSP(
				_servletContext, httpServletRequest, httpServletResponse,
				"/account_verification_form.jsp");
	}

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference(
			target = "(osgi.web.symbolicname=com.liferay.commerce.demo.account.verification.checkout)"
	)
	private ServletContext _servletContext;

	private static final Log _log = LogFactoryUtil.getLog(
			AccountVerificationCheckout.class);

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

}