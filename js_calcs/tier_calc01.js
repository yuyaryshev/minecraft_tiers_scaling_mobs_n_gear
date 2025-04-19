const JSON5 = require("json5");

//tierHp, tierArmor, tierToughness, tierEnchantability, tierAttack, tierId
const initialTiers = `
    10       0              0              1            1                wood
    14       1              0              1            2                stone
    19       2              1              1            3                copper
    25       3              1              2            3                chainmail
    30       2              1              5            3                gold
    40       4              2              3            10               iron
    45       3              2              4            4                brass
    80       6              4              6            20               diamond
   200       8              9             12            30               netherite
`
  .split("\n")
  .map((s) => s.trim())
  .filter((s) => s.length)
  .map((s, tierIndex) => {
    const [
      tierHp,
      tierArmor,
      tierToughness,
      tierEnchantability,
      tierAttack,
      tierId,
    ] = s.split(/\s+/).map((v, colIndex) => ([5].includes(colIndex) ? v : +v)); // Convert to numbers all except column 4
    return {
      tierIndex,
      tierHp,
      tierArmor,
      tierToughness,
      tierEnchantability,
      tierAttack,
      tierId,
    };
  });

const expectations = {
  "wood player vs wood mob": ["p", "5-7", "5-8"],
  "stone player vs stone mob": ["p", "5-7", "5-8"],
  "copper player vs copper mob": ["p", "5-7", "6-11"],
  "chainmail player vs chainmail mob": ["p", "5-7", "6-11"],
  "gold player vs gold mob": ["p", "5-7", "6-11"],
  "iron player vs iron mob": ["p", "5-7", "6-11"],
  "brass player vs brass mob": ["p", "5-7", "6-11"],
  "diamond player vs diamond mob": ["p", "5-7", "6-11"],
  "netherite player vs netherite mob": ["p", "5-7", "6-14"],

  "iron player vs diamond mob": ["m", "10-10000", "2-4"],
  "copper player vs diamond mob": ["m", "22-10000", "0-2"],
  "diamond player vs copper mob": ["p", "0-1", "12-10000"],
  "diamond player vs stone mob": ["p", "0-1", "12-10000"],

  "diamond player vs iron mob": ["p", "1-3", "12-10000"],
  "diamond player vs iron tank": ["p", "2-6", "12-10000"],
  "diamond player vs iron assassin": ["p", "0-3", "6-10000"],
  "diamond player vs iron ranged": ["p", "0-3", "8-10000"],
  "diamond player vs iron elite": ["p", "2-6", "8-10000"],
  "diamond player vs iron mage": ["p", "0-1", "3-10000"],
  "diamond player vs iron boss": ["m", "5-30", "5-10"],
  "diamond player vs copper boss": ["p", "5-30", "15-10000"],

  "diamond player vs diamond tank": ["p", "8-15", "12-30"],
  "diamond player vs diamond assassin": ["p", "2-4", "3-5"],
  "diamond player vs diamond ranged": ["p", "5-7", "6-11"],
  "diamond player vs diamond elite": ["m", "8-12", "3-6"],
  "diamond player vs diamond mage": ["p", "1-2", "2-3"],
  "diamond player vs diamond boss": ["m", "50-100", "3-5"],
};

const mobTypes = {
  normal: {
    damageF: 1,
    hpF: 1,
    armorF: 1,
    toughnessF: 1,
  },
  assassin: {
    damageF: 2,
    hpF: 0.7,
    armorF: 0.3,
    toughnessF: 0,
  },
  tank: {
    damageF: 0.7,
    hpF: 2.0,
    armorF: 1.2,
    toughnessF: 1.2,
  },
  ranged: {
    damageF: 1,
    hpF: 1,
    armorF: 0.3,
    toughnessF: 0.1,
  },
  mage: {
    damageF: 3,
    hpF: 0.4,
    armorF: 0,
    toughnessF: 0,
  },
  elite: {
    damageF: 1.4,
    hpF: 1.7,
    armorF: 1.1,
    toughnessF: 1.0,
  },
  boss: {
    damageF: 2,
    hpF: 10,
    armorF: 1.2,
    toughnessF: 1.2,
  },
};
for (let mtypeId in mobTypes) {
  mobTypes[mtypeId].mtypeId = mtypeId;
}

const maxArmorPiecesCount = 4;
const maxArmor =
  initialTiers[initialTiers.length - 1].tierArmor * maxArmorPiecesCount;

const globals = {
  avgMobMeleeHitsToKillPlayer: 6, // How many times on averege should a mob of the same tier hit player to kill him. Used in mob damage calculation.
  avgPlayerMeleeHitsToKillMob: 5, // How many times on averege should player hit mob of the same tier hit player to kill it. Used in player's weapon damage calculation.
  maxArmorPiecesCount,
  maxArmor, // Used for even distribution of armor points.
  minDamageRatioAfterArmorReduction: 0.2, // Minimal part of damage that passes to victim if the damage passed reduction
  playerBaseHp: 10,
  playerBaseDamage: 1,
  toughnessEffectivityOnMob: 0.2,
  armorEffectivityOnMob: 0.5,
  weaponEffectivityOnMob: 0.5,
  toughnessFactor: 0.2, // How much reduction does each point of toughness give
};

console.log(`CODE00000011 globals=`, globals, undefined, 4);

console.log(`CODE00000001 initialTiers=`);
console.table(initialTiers);

function roundTo1Digit(v) {
  return Math.round(v * 10) / 10;
}

function getArmorKoeff(armor) {
  return (
    1 -
    (armor * (1 - globals.minDamageRatioAfterArmorReduction)) / globals.maxArmor
  );
}

// Returns dhp after the damage is reduced by armor (dhp = damagepoints or hitpoints). baseDhp -> reducedDhp
function getDhpReducedByArmor(baseDhp, armor, reduction) {
  return roundTo1Digit(
    Math.max(0, (baseDhp - reduction) * getArmorKoeff(armor)),
  );
}

// Returns dhp that would be equivalent to wearing armor (dhp = damagepoints or hitpoints). reducedDhp -> baseDhp
function getDhpEquivalentToByArmor(reducedDhp, armor, reduction) {
  // This is just an inverted function getDhpReducedByArmor
  return roundTo1Digit(reducedDhp / getArmorKoeff(armor) + reduction);
}

function calculateTier(tier) {
  const {
    tierIndex,
    tierHp,
    tierArmor,
    tierToughness,
    tierEnchantability,
    tierAttack,
    tierId,
  } = tier;

  // finalDamage = max(0, (initialDamage-t)*max(b, 1-a*(1-b)/M))
  // finalDamage = (initialDamage-t)*max(b, 1-a*(1-b)/M)
  // finalDamage / (1-playerArmor*(1-globals.minDamageRatioAfterArmorReduction)/globals.maxArmor) + playerDamageReduction = initialDamage

  const playerArmor = tierArmor * maxArmorPiecesCount;
  const playerArmorKoeff = getArmorKoeff(playerArmor);
  const playerDamageReduction = roundTo1Digit(
    tierToughness * maxArmorPiecesCount * globals.toughnessFactor,
  );
  const playerHpEquivalentToArmor = tierHp;
  const playerHp = roundTo1Digit(
    getDhpReducedByArmor(
      playerHpEquivalentToArmor,
      playerArmor,
      playerDamageReduction,
    ),
  );
  const playerHpBonus = roundTo1Digit(playerHp - globals.playerBaseHp);
  const playerWeaponAttack = tierAttack;

  const playerAvgMeleeDamage = globals.playerBaseDamage + tier.tierAttack;

  // TODO у игрока и у моба может/должен быть разный базовый дамаг! (Базовая атака) Нужно это учесть. Это сильно повлияет на значения

  const mobHpEquivalentToArmor =
    (globals.playerBaseDamage + playerWeaponAttack) *
    globals.avgPlayerMeleeHitsToKillMob; // TODO вот тут должно быть оружие игрока! И его дамаг, а не вот так как есть сейчас
  const mobArmor = roundTo1Digit(
    tierArmor * maxArmorPiecesCount * globals.armorEffectivityOnMob,
  );
  const mobArmorKoeff = getArmorKoeff(mobArmor);
  const mobDamageReduction = roundTo1Digit(
    tierToughness *
      maxArmorPiecesCount *
      globals.toughnessEffectivityOnMob *
      globals.toughnessFactor,
  );
  const mobHp = roundTo1Digit(
    getDhpReducedByArmor(mobHpEquivalentToArmor, mobArmor, mobDamageReduction),
  );
  const mobAvgMeleeDamage = roundTo1Digit(
    playerHpEquivalentToArmor / globals.avgMobMeleeHitsToKillPlayer,
  );

  return {
    tierIndex,
    tierHp,
    tierArmor,
    tierToughness,
    tierEnchantability,
    tierAttack,
    tierId,

    playerArmor,
    playerDamageReduction,
    playerHpEquivalentToArmor,
    playerHp,
    playerHpBonus,
    playerWeaponAttack,

    mobAvgMeleeDamage,
    mobArmor,
    mobDamageReduction,
    mobHp,
  };
}

function tierShortForm(t) {
  return {
    ti: t.tierIndex,
    thp: t.tierHp,
    ta: t.tierArmor,
    tt: t.tierToughness,
    te: t.tierEnchantability,
    tatk: t.tierAttack,
    tid: t.tierId,

    pa: t.playerArmor,
    pdr: t.playerDamageReduction,
    phpa: t.playerHpEquivalentToArmor,
    php: t.playerHp,
    phpb: t.playerHpBonus,
    pwa: t.playerWeaponAttack,

    md: t.mobAvgMeleeDamage,
    ma: t.mobArmor,
    mhp: t.mobHp,
  };
}

const tiers = initialTiers.map(calculateTier);

console.log(`CODE00000002 tiers=`);
console.table(tiers.map(tierShortForm));
// console.table(tiers.filter((i) => i.tierId === "diamond").map(tierShortForm));

// console.log(`CODE00000001 tiers=`, tiers);

// Original formula: max(armor/5, armor - damage/ (2+toughness/4)) / 25

function resultToText(r) {
  return `${r[0] === "p" ? "player" : "mob"} wins in ${r[1]} turns`;
}

function matchesExpectation(r, e, txt) {
  if (e[0] !== r[0]) {
    return false;
  }
  if (e[1] !== "*") {
    const [from, to] = e[1].split("-").map((i) => +i);
    const t1 = from <= r[1] && (to < 10000 ? r[1] <= to : true);
    if (!t1) {
      return false;
    }
  }

  if (e[2] !== "*") {
    const [from, to] = e[2].split("-").map((i) => +i);
    const t2 = from <= r[2] && (to < 10000 ? r[2] <= to : true);
    if (!t2) {
      return false;
    }
  }
  return true;
}

function battleSimEx(txt, php, pwa, pa, prd, mtypeId, mhp, md, ma, mrd) {
  // console.log(`CODE00010001 Battle sim start.`);
  const pdelta = roundTo1Digit(getDhpReducedByArmor(md, pa, prd));
  const pd = globals.playerBaseDamage + pwa;
  const mdelta = roundTo1Digit(getDhpReducedByArmor(pd, ma, mrd));
  const pturns_to_die = roundTo1Digit(php / pdelta);
  const mturns_to_die = roundTo1Digit(mhp / mdelta);
  const result = [
    pturns_to_die > mturns_to_die ? "p" : "m",
    mturns_to_die,
    pturns_to_die,
  ];

  const expectation = expectations[txt] || "";
  const fail = !matchesExpectation(result, expectation, txt) ? "FAIL" : "";

  const r = {
    txt,
    php,
    pwa,
    pa,
    prd,
    pdelta,
    pturns_to_die,
    mtypeId,
    mhp,
    md,
    ma,
    mrd,
    mdelta,
    mturns_to_die,
    result,
    expectation,
    fail,
  };
  //  console.log(`CODE00000000 battleSimEx r=`, JSON5.stringify(r, undefined, 4));
  return r;
}

function battleSim(ptier, mtier, mtype) {
  if (typeof ptier === "string") {
    ptier = tiers.filter((i) => i.tierId === ptier)[0];
  }
  if (typeof mtier === "string") {
    mtier = tiers.filter((i) => i.tierId === mtier)[0];
  }

  if (typeof mtype === "string") {
    mtype = mobTypes[mtype] || mobTypes["normal"];
  }
  if (!mtype) {
    mtype = mobTypes["normal"];
  }

  const txt = `${ptier.tierId} player vs ${mtier.tierId} ${mtype.mtypeId === "normal" ? "mob" : mtype.mtypeId}`;

  const r = battleSimEx(
    txt,
    ptier.playerHp,
    ptier.playerWeaponAttack,
    ptier.playerArmor,
    ptier.playerDamageReduction,
    mtype.mtypeId,
    roundTo1Digit(mtier.mobHp * mtype.hpF),
    roundTo1Digit(mtier.mobAvgMeleeDamage * mtype.damageF),
    roundTo1Digit(mtier.mobArmor * mtype.armorF),
    roundTo1Digit(mtier.mobDamageReduction * mtype.toughnessF),
  );
  return r;
}

const battleLogs = [];

battleLogs.push(...tiers.map((t) => battleSim(t, t)));

battleLogs.push(battleSim("diamond", "diamond", "normal"));
battleLogs.push(battleSim("diamond", "diamond", "tank"));
battleLogs.push(battleSim("diamond", "diamond", "assassin"));
battleLogs.push(battleSim("diamond", "diamond", "elite"));
battleLogs.push(battleSim("diamond", "diamond", "mage"));
battleLogs.push(battleSim("diamond", "diamond", "boss"));
battleLogs.push(battleSim("diamond", "diamond", "boss"));

battleLogs.push(battleSim("iron", "diamond"));
battleLogs.push(battleSim("diamond", "iron"));
battleLogs.push(battleSim("copper", "diamond"));
battleLogs.push(battleSim("diamond", "copper"));
battleLogs.push(battleSim("diamond", "stone"));

battleLogs.push(battleSim("diamond", "iron", "normal"));
battleLogs.push(battleSim("diamond", "iron", "tank"));
battleLogs.push(battleSim("diamond", "iron", "assassin"));
battleLogs.push(battleSim("diamond", "iron", "elite"));
battleLogs.push(battleSim("diamond", "iron", "mage"));
battleLogs.push(battleSim("diamond", "iron", "boss"));
battleLogs.push(battleSim("diamond", "copper", "boss"));

console.log(`CODE00020001 battleLogs=`);
console.table(battleLogs);

console.log(`CODE00000099 finished.`);
