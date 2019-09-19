/*
 * Copyright © 2011-2019 Chris Vest (mr.chrisvest@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package stormpot;

/**
 * The claim method in this pool tap follows the same idea as the thread-local
 * caching and TLR-claiming in the pool itself. However, instead of relying on
 * a thread-local for the object caching, we instead directly use a field of
 * the pool tap instance.
 *
 * This has two advantages: 1) a field load is faster than a thread-local
 * lookup, and 2) there is no thread-local "leaking" or contamination.
 *
 * @param <T> The poolable type.
 */
final class BlazePoolThreadLocalTap<T extends Poolable> extends PoolTap<T> {
  private final BlazePool<T> pool;
  private final BSlotCache<T> cache = new BSlotCache<>();

  BlazePoolThreadLocalTap(BlazePool<T> pool) {
    this.pool = pool;
  }

  @Override
  public T claim(Timeout timeout) throws PoolException, InterruptedException {
    return pool.tlrClaim(timeout, cache);
  }
}
