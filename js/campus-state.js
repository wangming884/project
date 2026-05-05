/**
 * 校园平台前端状态工具
 * 统一管理积分与签到相关的本地状态
 */

const CAMPUS_STORAGE_KEYS = {
    points: 'userPoints',
    legacyRecommendPoints: 'shopPoints',
    signInDate: 'lastSignInDate',
    nightCheckInDate: 'lastNightCheckIn',
};

function getDateKey(date = new Date()) {
    const current = new Date(date);
    const year = current.getFullYear();
    const month = `${current.getMonth() + 1}`.padStart(2, '0');
    const day = `${current.getDate()}`.padStart(2, '0');
    return `${year}-${month}-${day}`;
}

function isSameDateKey(dateValue, compareDate = new Date()) {
    if (!dateValue) {
        return false;
    }

    const compareKey = getDateKey(compareDate);
    const parsedDate = new Date(dateValue);
    if (Number.isNaN(parsedDate.getTime())) {
        return String(dateValue) === compareKey;
    }
    return getDateKey(parsedDate) === compareKey;
}

function toSafeNumber(value, fallback = 0) {
    const numericValue = Number(value);
    return Number.isFinite(numericValue) ? numericValue : fallback;
}

function getCampusPoints() {
    const points = getStorage(CAMPUS_STORAGE_KEYS.points, null);
    if (points !== null) {
        return toSafeNumber(points, 0);
    }

    const legacyPoints = getStorage(CAMPUS_STORAGE_KEYS.legacyRecommendPoints, null);
    if (legacyPoints !== null) {
        return setCampusPoints(legacyPoints);
    }

    return 0;
}

function setCampusPoints(value) {
    const nextValue = toSafeNumber(value, 0);
    setStorage(CAMPUS_STORAGE_KEYS.points, nextValue);
    setStorage(CAMPUS_STORAGE_KEYS.legacyRecommendPoints, nextValue);
    return nextValue;
}

function getDailySignInDate() {
    return getStorage(CAMPUS_STORAGE_KEYS.signInDate, null);
}

function setDailySignInDate(dateValue = getDateKey()) {
    setStorage(CAMPUS_STORAGE_KEYS.signInDate, dateValue);
    return dateValue;
}

function clearDailySignInDate() {
    removeStorage(CAMPUS_STORAGE_KEYS.signInDate);
    return null;
}

function getNightCheckInDate() {
    return getStorage(CAMPUS_STORAGE_KEYS.nightCheckInDate, null);
}

function setNightCheckInDate(dateValue = getDateKey()) {
    setStorage(CAMPUS_STORAGE_KEYS.nightCheckInDate, dateValue);
    return dateValue;
}

function clearNightCheckInDate() {
    removeStorage(CAMPUS_STORAGE_KEYS.nightCheckInDate);
    return null;
}

function hasSignedInToday(dateValue = getDailySignInDate()) {
    return isSameDateKey(dateValue);
}

function hasNightCheckedInToday(dateValue = getNightCheckInDate()) {
    return isSameDateKey(dateValue);
}

if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        CAMPUS_STORAGE_KEYS,
        getDateKey,
        isSameDateKey,
        getCampusPoints,
        setCampusPoints,
        getDailySignInDate,
        setDailySignInDate,
        clearDailySignInDate,
        getNightCheckInDate,
        setNightCheckInDate,
        clearNightCheckInDate,
        hasSignedInToday,
        hasNightCheckedInToday,
        toSafeNumber,
    };
}
