interface View {
  name?: string;
  size?: number;
  type: number;
  bold?: number;
  PaddingLeft: number;
  PaddingTop: number;
  PaddingRight: number;
  PaddingBottom: number;
  setViewVisibility?: boolean;
  color?: string;
  uri?: string;
  ZeroTime?: Date | null;
  hide?: boolean;
}
interface CNViewItems extends Array<View> {}

interface CN {
  eventData: string;
  title: string;
  body: string;
  id: number;
  View: CNViewItems;
}
interface Type {
  Image: number;
  Text: number;
  Cronometer: number;
}
interface FB_TYPE {
  NORMAL: number;
  BOLD: number;
  ITALIC: number;
  BOLD_ITALIC: number;
}
interface CTN {
  payload: string;
  title: string;
  body: string;
  remove?: boolean;
  foreground: boolean;
  id: number;
  date?: Date;
  isCountDown: boolean;
  setCustomContentView?: boolean;
}

export function getArrayLength(arr: any[]): number;
export function onEvent(listener: Function);
export function CustomNotification(a: CN, cb: Function);
export function TimerNotification(a: CTN);
export function RemoveTimer(n: number);
export let TYPES: Type;
export let FB_TYPE: FB_TYPE;
