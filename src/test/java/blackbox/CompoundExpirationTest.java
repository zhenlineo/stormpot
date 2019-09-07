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
package blackbox;

import org.junit.jupiter.api.Test;
import stormpot.CompoundExpiration;
import stormpot.Expiration;
import stormpot.GenericPoolable;
import stormpot.MockSlotInfo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static stormpot.ExpireKit.$expired;
import static stormpot.ExpireKit.$fresh;
import static stormpot.ExpireKit.expire;

class CompoundExpirationTest {

  @Test
  void expiresWhenBothExpirationsExpire() throws Exception {
    CompoundExpiration<GenericPoolable> compoundExpiration = compoundExpiration(expire($expired), expire($expired));

    assertTrue(compoundExpiration.hasExpired(mockSlotInfo()));
  }

  @Test
  void expiresWhenOneExpirationExpires() throws Exception {
    CompoundExpiration<GenericPoolable> compoundExpiration = compoundExpiration(expire($expired), expire($fresh));

    assertTrue(compoundExpiration.hasExpired(mockSlotInfo()));

    compoundExpiration = compoundExpiration(expire($fresh), expire($expired));

    assertTrue(compoundExpiration.hasExpired(mockSlotInfo()));
  }

  @Test
  void doesNotExpireWhenNoExpirationExpire() throws Exception {
    CompoundExpiration<GenericPoolable> compoundExpiration = compoundExpiration(expire($fresh), expire($fresh));

    assertFalse(compoundExpiration.hasExpired(mockSlotInfo()));
  }

  private CompoundExpiration<GenericPoolable> compoundExpiration(
          Expiration<GenericPoolable> first,
          Expiration<GenericPoolable> second) {
    return new CompoundExpiration<>(first, second);
  }

  private MockSlotInfo mockSlotInfo() {
    return new MockSlotInfo(0);
  }
}