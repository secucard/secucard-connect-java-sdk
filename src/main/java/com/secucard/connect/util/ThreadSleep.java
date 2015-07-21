/*
 * Copyright (c) 2015. hp.weber GmbH & Co secucard KG (www.secucard.com)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.secucard.connect.util;

import java.util.concurrent.TimeUnit;

/**
 * Can be used to realize an "interruptable" or "max" sleep behaviour by breaking one large sleep period into
 * small portions and checking the cancel condition between each portion. So the cancellation can be performed after a
 * short time portion rather than wait the whole period.
 */
public abstract class ThreadSleep {
  public void execute(long timeout, long step, TimeUnit timeUnit) {
    if (step > timeout) {
      throw new IllegalArgumentException("Step must be less or equal to timeout.");
    }

    long i = 0;
    while (i < timeout) {
      try {
        timeUnit.sleep(step);
      } catch (InterruptedException e) {
        // ignore
      }
      if (cancel()) {
        return;
      }
      i = reset() ? 0 : i + step;
    }
  }

  protected boolean reset() {
    return false;
  }

  protected abstract boolean cancel();
}
