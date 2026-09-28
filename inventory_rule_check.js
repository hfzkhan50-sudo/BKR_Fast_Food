const tests = [
  ['Hot Wings 10 Pcs', ''],
  ['Chicken Shawarma', ''],
  ['Shwarma', ''],
  ['Nuggets 10 Pcs', ''],
  ['Patty Fatty Burger', ''],
  ['Zinger Burger', ''],
  ['Family Deal', 'deals']
];

for (const [item, category] of tests) {
  const normalizedName = item.trim().toLowerCase().replaceAll('-', ' ');
  let deductions = [];

  if (category === 'deals') {
    deductions = [['1.5 Ltr', 1], ['Burger Bun', 4], ['Wings', 6], ['Chicken Thigh', 4]];
  } else if (category === 'drinks' || (!category && ['regular', '1 ltr', '1.5 ltr', 'water (small)', 'water (large)', 'ten pack'].includes(normalizedName))) {
    deductions = [[item, 1]];
  } else if (category === 'burger' || (!category && normalizedName.includes('burger'))) {
    deductions = [['Burger Bun', 1]];
    if (normalizedName.includes('patty fatty') || normalizedName === 'beef burger') {
      deductions.push(['Chicken Patty', 1]);
    } else if (normalizedName.includes('tower burger')) {
      deductions.push(['Chicken Thigh', 2]);
    } else if (normalizedName.includes('zinger burger') || normalizedName.includes('zinger chees burger') || normalizedName.includes('bkr special burger')) {
      deductions.push(['Chicken Thigh', 1]);
    }
  } else if (category === 'wrap' || (!category && normalizedName.includes('wrap'))) {
    deductions = [['Wrap', 1]];
  } else if (category === 'paratha roll' || (!category && normalizedName.includes('roll'))) {
    deductions = [['Paratha Roll', 1]];
  } else if (category === 'hot wings' || (!category && (normalizedName.includes('wing') || normalizedName.includes('nugget')))) {
    deductions = normalizedName.includes('nugget') ? [['Nuggets', 10]] : [['Wings', 6]];
  } else if (category === 'shawarma' || (!category && (normalizedName.includes('shawarma') || normalizedName.includes('shwarma')))) {
    deductions = [['Shwarma Bread', 1]];
  }

  console.log(item + ' => ' + JSON.stringify(deductions));
}
