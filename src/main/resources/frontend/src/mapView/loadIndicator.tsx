import Loading from "./loading.png";
import Styles from "./loadIndicator.module.css";

export function LoadIndicator() {
    return <div className={Styles.container}>
        <img src={Loading} className={Styles.rotate} />
    </div>
}