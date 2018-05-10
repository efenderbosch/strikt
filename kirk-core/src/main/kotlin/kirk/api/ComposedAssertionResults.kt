package kirk.api

import kirk.internal.AssertionResultCollector
import kirk.internal.AssertionResultHandler

/**
 * The results of assertions made inside the block passed to
 * [AssertionContext.compose].
 */
class ComposedAssertionResults
internal constructor(
  private val assertionResultHandler: AssertionResultHandler,
  private val nestedReporter: AssertionResultCollector,
  private val description: String,
  private val subject: Any?
) {
  /**
   * A convenient way to chain a result handler after
   * [AssertionContext.compose].
   */
  infix fun results(block: ComposedAssertionResults.() -> Unit) = apply(block)

  /**
   * Report that the assertion succeeded.
   */
  fun pass() {
    assertionResultHandler.report(
      Result(Status.Passed, description, subject, nestedResults = nestedReporter.results)
    )
  }

  /**
   * Report that the assertion failed.
   */
  fun fail() {
    assertionResultHandler.report(
      Result(Status.Failed, description, subject, nestedResults = nestedReporter.results)
    )
  }

  /**
   * Report that the assertion failed.
   *
   * @param actualDescription descriptive text about [actual].
   * @param actual the value that violated the assertion.
   */
  fun fail(actual: Any?) {
    assertionResultHandler.report(Result(
      Status.Failed,
      description,
      subject,
      actual,
      nestedResults = nestedReporter.results
    ))
  }

  /**
   * Returns `true` if any nested assertions evaluated inside
   * [AssertionContext.compose] failed, `false` otherwise.
   *
   * @see expect
   */
  val anyFailed: Boolean = nestedReporter.anyFailed

  /**
   * Returns `true` if _all_ nested assertions evaluated using
   * [AssertionContext.compose] failed, `false` otherwise.
   *
   * @see expect
   */
  val allFailed: Boolean = nestedReporter.allFailed

  /**
   * Returns `true` if any nested assertions evaluated using
   * [AssertionContext.compose] passed, `false` otherwise.
   *
   * @see expect
   */
  val anyPassed: Boolean = nestedReporter.anyPassed

  /**
   * Returns `true` if _all_ nested assertions evaluated using
   * [AssertionContext.compose] passed, `false` otherwise.
   *
   * @see expect
   */
  val allPassed: Boolean = nestedReporter.allPassed
}
