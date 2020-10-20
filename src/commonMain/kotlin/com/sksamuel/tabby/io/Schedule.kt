package com.sksamuel.tabby.io

import com.sksamuel.tabby.Option
import com.sksamuel.tabby.none
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
sealed class Decision<in Result, out State> {

   /**
    * A decision to halt the effect.
    */
   data class Halt<State>(val state: State) : Decision<Any, State>()

   /**
    * A decision to re-run the effect after the optional delay.
    * A schedule must be provided to evaluate the next loop.
    */
   data class Continue<in Result, out State>(val state: State,
                                             val duration: Option<Duration>,
                                             val next: Schedule<Result, State>) : Decision<Result, State>()

   fun plusDuration(plus: Duration): Decision<Result, State> = when (this) {
      is Continue -> Continue(this.state, this.duration.map { it + plus }, this.next)
      is Halt -> this
   }
}

/**
 * @tparam Result - the effect success (if using repeat) or failure (if using retry).
 * @tparam State - a materialized value returned from the scheduler, that is passed to the next invocation.
 */
@OptIn(ExperimentalTime::class)
fun interface Schedule<in Result, out State> {

   fun invoke(result: Result): Decision<Result, State>

   companion object {

      val Forever: Schedule<Any, Int> = unfold(0) { it + 1 }

      val Once = Forever.whileState { it < 1 }

      val Never = Schedule<Any, Int> { Decision.Halt(0) }

      /**
       * A schedule that always recurs, which returns results as state.
       */
      fun <A> identity(): Schedule<A, A> = Schedule { result -> Decision.Continue(result, none, identity()) }

      /**
       * Returns a new schedule that continues for as long the specified predicate on the input
       * evaluates to true.
       */
      fun <A> whileState(test: (A) -> Boolean): Schedule<A, A> {
         return identity<A>().check { input, _ -> test(input) }
      }

      private fun <Result, State> unfold(initial: State, f: (State) -> State): Schedule<Result, State> {
         fun loop(state: State): Schedule<Result, State> = Schedule {
            Decision.Continue(state, none, loop(f(state)))
         }
         return loop(initial)
      }
   }
}


/**
 * Returns a new schedule that continues as long the given test
 * on the state evaluates to true.
 */
fun <Result, State> Schedule<Result, State>.whileState(test: (State) -> Boolean): Schedule<Result, State> {
   return check { _, state -> test(state) }
}

/**
 * Returns a new schedule that decorates this schedule, adding a delay to each decision to continue.
 */
@OptIn(ExperimentalTime::class)
fun <Result, State> Schedule<Result, State>.delay(duration: Duration): Schedule<Result, State> = delay { duration }

/**
 * Returns a new schedule that decorates this schedule, adding the delay returned from the specified
 * function to each decision to continue.
 */
@OptIn(ExperimentalTime::class)
fun <Result, State> Schedule<Result, State>.delay(f: (Result) -> Duration): Schedule<Result, State> =
   Schedule { result -> this@delay.invoke(result).plusDuration(f(result)) }

/**
 * Returns a new schedule that decorates this schedule, adding a delay if the test evaluates to true,
 * otherwise adding no delay.
 */
@OptIn(ExperimentalTime::class)
fun <Result, State> Schedule<Result, State>.delayIf(duration: Duration,
                                                    predicate: (Result) -> Boolean) = Schedule<Result, State> { result ->
   if (predicate(result)) {
      this@delayIf.invoke(result).plusDuration(duration)
   } else {
      this@delayIf.invoke(result)
   }
}

/**
 * Returns a new schedule that passes each input and output of this schedule to the specified
 * function, and then determines whether or not to continue based on the return value of the
 * function.
 */
fun <Result, State> Schedule<Result, State>.check(test: (Result, State) -> Boolean): Schedule<Result, State> {
   val self = this
   return Schedule { result ->
      when (val d = self.invoke(result)) {
         is Decision.Halt -> d
         is Decision.Continue -> when (test(result, d.state)) {
            true -> d
            false -> Decision.Halt(d.state) as Decision<Result, State>
         }
      }
   }
}
