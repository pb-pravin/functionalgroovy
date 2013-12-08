package com.github.mperry.fg.test

import fj.Show
import fj.Unit
import fj.test.CheckResult
import fj.test.Property

import static org.junit.Assert.assertTrue

/**
 * Created with IntelliJ IDEA.
 * User: MarkPerry
 * Date: 9/08/13
 * Time: 1:46 AM
 * To change this template use File | Settings | File Templates.
 */
class PropertyExtension {

	static CheckResult checkOkWithSummary(Property p) {
		def cr = p.check()
		cr.printlnSummary()
		assertTrue(cr.isOk())
		Unit.unit()
		cr
	}

	static CheckResult checkOkWithNullableSummary(Property p) {
		checkBooleanWithNullableSummary(p, true)
	}

	static CheckResult checkBooleanWithNullableSummary(Property p, boolean b) {
		def cr = p.check()
//		cr.printlnSummaryNullable()
		CheckResult.summaryNullable().println(cr)
//		CheckResult.summary.println(cr)
		assertTrue(cr.isOk() == b)
		cr
	}


}
