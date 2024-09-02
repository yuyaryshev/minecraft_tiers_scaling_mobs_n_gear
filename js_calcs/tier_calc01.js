const JSON5 = require("json5");

const dmgFunc1 = (d, a) => d / (1 + a);
const rr = [
  dmgFunc1(10, 1),
  dmgFunc1(10, 2) - dmgFunc1(10, 1),
  dmgFunc1(10, 3) - dmgFunc1(10, 2),
];

console.log(`CODE00000000 rr=${rr}`);

//tierHp, tierArmor, tierToughness, tierEnchantability, tierAttack, tierId
const initialTiers = `
    10       0              0              1            1  wood
    14       1              0              1            2  stone
    19       2              1              1            3  copper
    25       3              1              2            3  chainmail
    30       2              1              5            3  gold
    40       4              2              3            6  iron
    45       3              2              4            4  brass
    80       6              4              6            8  diamond
   120       8              9             12            10  netherite
`
  .split("\n")
  .map((s) => s.trim())
  .filter((s) => s.length)
  .map((s, tierIndex) => {
    const [tierHp, tierArmor, tierToughness, tierEnchantability, tierAttack, tierId] = s
      .split(/\s+/)
      .map((v, colIndex) => ([5].includes(colIndex) ? v : +v)); // Convert to numbers all except column 4
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

const maxArmorPiecesCount = 6;
const maxArmor =
  initialTiers[initialTiers.length - 1].tierArmor * maxArmorPiecesCount;

const globals = {
  avgMobMeleeHitsToKillPlayer: 5, // How many times on averege should a mob of the same tier hit player to kill him. Used in mob damage calculation.
  avgPlayerMeleeHitsToKillMob: 5, // How many times on averege should player hit mob of the same tier hit player to kill it. Used in player's weapon damage calculation.
  maxArmorPiecesCount,
  maxArmor, // Maximum number armor can be. Used for even distribution of armor points.
  minDamageRatioAfterArmorReduction: 0.2, // Minimal part of damage that passes to victim if the damage passed reduction
  playerBaseHp: 10,
  playerBaseDamage: 1,
};

console.log(`CODE00000011 globals=`, JSON5.stringify(globals, undefined, 4));

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

// Returns dhp after the damage is reduced by armor (dhp = damagepoints or hitpoints)
function getDhpReducedByArmor(baseDhp, armor, reduction) {
  return (baseDhp - reduction) * getArmorKoeff(armor);
}

// Returns dhp that would be equivalent to wearing armor (dhp = damagepoints or hitpoints)
function getDhpEquivalentToByArmor(reducedDhp, armor, reduction) {
  return reducedDhp / getArmorKoeff(armor) + reduction;
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
  const playerDamageReduction = tierToughness * maxArmorPiecesCount;
  const playerHpEquivalentToArmor = tierHp;
  const playerHp = roundTo1Digit(
    getDhpReducedByArmor(
      playerHpEquivalentToArmor,
      playerArmor,
      playerDamageReduction,
    ),
  );
  const playerHpBonus = roundTo1Digit(playerHp - globals.playerBaseHp);
  const playerWeaponAttack = 0;

  const playerAvgMeleeDamage = roundTo1Digit(
    getDhpEquivalentToByArmor(
      playerHpEquivalentToArmor,
      playerArmor,
      playerDamageReduction,
    ),
  );

  // TODO у игрока и у моба может/должен быть разный базовый дамаг! (Базовая атака) Нужно это учесть. Это сильно повлияет на значения
  //



  const mobHpEquivalentToArmor = (globals.playerBaseDamage + tierAttack)*globals.avgPlayerMeleeHitsToKillMob; // TODO вот тут должно быть оружие игрока! И его дамаг, а не вот так как есть сейчас
  const mobArmor = tierArmor * maxArmorPiecesCount;
  const mobArmorKoeff = getArmorKoeff(mobArmor);
  const mobDamageReduction = tierToughness * maxArmorPiecesCount;
  const mobHp = roundTo1Digit(
    getDhpReducedByArmor(mobHpEquivalentToArmor, mobArmor, mobDamageReduction),
  );
  const mobAvgMeleeDamage = roundTo1Digit(
    getDhpEquivalentToByArmor(
      playerHpEquivalentToArmor,
      playerArmor,
      playerDamageReduction,
    ),
  );



  return {
    ti:tierIndex,
    thp:tierHp,
    ta:tierArmor,
    tt:tierToughness,
    te:tierEnchantability,
    tatk:tierAttack,
    tid:tierId,

    pa:playerArmor,
    pdr:playerDamageReduction,
    phpa:playerHpEquivalentToArmor,
    php:playerHp,
    phpb:playerHpBonus,

    md:mobAvgMeleeDamage,
    ma:mobArmor,
    mhp:mobHp
  };
}
const tiers = initialTiers.map(calculateTier);

console.log(`CODE00000002 tiers=`);
console.table(tiers);
console.log(`CODE00000099 finished.`);

// console.log(`CODE00000001 tiers=`, tiers);

// Original formula: max(armor/5, armor - damage/ (2+toughness/4)) / 25
