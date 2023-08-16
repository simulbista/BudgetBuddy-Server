// utils.js

function convertTimestampToDate(timestamp) {
    const date = new Date(timestamp);
  const year = date.getUTCFullYear();
  const month = String(date.getUTCMonth() + 1).padStart(2, "0");
  const day = String(date.getUTCDate()).padStart(2, "0");
  const convertedDate = `${year}-${month}-${day}`;
  return convertedDate;
}

export { convertTimestampToDate };
