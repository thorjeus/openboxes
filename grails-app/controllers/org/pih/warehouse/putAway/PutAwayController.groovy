package org.pih.warehouse.putAway

import grails.plugin.rendering.pdf.PdfRenderingService
import org.codehaus.groovy.grails.web.json.JSONObject
import org.pih.warehouse.api.Putaway
import org.pih.warehouse.api.PutawayItem
import org.pih.warehouse.core.Location
import org.pih.warehouse.order.Order
import org.pih.warehouse.picklist.Picklist
import org.pih.warehouse.requisition.Requisition
import org.pih.warehouse.core.User

class PutAwayController {

	PdfRenderingService pdfRenderingService
	def inventoryService

	def index = {
		redirect(action: "create")
	}

	def list = {
		redirect(action: "create")
	}

	// This template is generated by webpack during application start
	def create = {
		render(template: "/common/react")
	}


	def generatePdf = {
        log.info "Params " + params

        Putaway putaway
        JSONObject jsonObject
		User user = session.user

        if (request.method == "POST") {
            jsonObject = request.JSON
        }
		else if (params.id) {
			Order order = Order.get(params.id)
			putaway = Putaway.createFromOrder(order)
			putaway.putawayItems.each { PutawayItem putawayItem ->
				putawayItem.availableItems =
						inventoryService.getAvailableBinLocations(putawayItem.currentFacility, putawayItem.product)
			}
			jsonObject = new JSONObject(putaway.toJson())
		}

		renderPdf(
				template: "/putAway/print",
				model: [jsonObject:jsonObject, user:user],
				filename: "Putaway ${putaway?.putawayNumber}.pdf"
		)
	}
}
